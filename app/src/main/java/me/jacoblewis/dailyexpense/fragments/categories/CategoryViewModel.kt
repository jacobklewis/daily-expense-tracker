package me.jacoblewis.dailyexpense.fragments.categories

import android.util.Log
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.commons.BudgetBalancer
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.data.daos.CategoriesDao
import me.jacoblewis.dailyexpense.data.daos.PaymentsDao
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.MonthDisplay
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.managers.BalanceManager
import java.util.*
import javax.inject.Inject

class CategoryViewModel
@Inject
constructor(val categoriesDao: CategoriesDao, val paymentsDao: PaymentsDao, balanceManager: BalanceManager) : ViewModel() {

    private val fromDate: MutableLiveData<Calendar> = MutableLiveData()
    val categories: LiveData<List<Category>> = Transformations.switchMap(fromDate) { lowerDate ->
        Transformations.map(categoriesDao.getAllCategoryPayments()) { cats ->
            return@map cats.map { categoryPayments ->
                val thisMonthsPayments = categoryPayments.payments.filter { payment -> payment.creationDate > lowerDate }
                categoryPayments.category.apply {
                    payments.addAll(thisMonthsPayments)
                }
            }.sortedByDescending { it.payments.map { p -> p.cost }.sum() }
        }
    }
    val budget = balanceManager.currentBudget
    val remainingBudget: LiveData<Float> = Transformations.switchMap(fromDate) { lowerDate ->
        Transformations.map(paymentsDao.getAllPaymentsSince(lowerDate)) { payments ->
            BudgetBalancer.calculateRemainingBudget(budget, payments.mapNotNull { it.transaction })
        }
    }
    var associatedPayment: Payment? = null


    fun updateCategoryDate(calendar: Calendar) {
        fromDate.value = calendar
    }

    fun applyCategoryToPayment(category: Category) {
        associatedPayment?.categoryId = category.categoryId
    }

    fun commitPayment(scope: CoroutineScope) {
        associatedPayment?.let {
            scope.launch(context = Dispatchers.IO) {
                paymentsDao.insertPayment(it)
            }
        }
    }

    fun removeCategory(scope: CoroutineScope, category: Category) {
        scope.launch(context = Dispatchers.IO) {
            try {
                paymentsDao.deleteByCategory(categoryId = category.categoryId)
                categoriesDao.deleteCategory(category)
            } catch (e: Exception) {
                // TODO: log somewhere else
                Log.e("CategoryViewModel", e.localizedMessage)
            }
        }
    }

    // TODO: add to a manager
    fun getPreviousMonths(@IntRange(from = 1, to = 12) numOfMonths: Int,
                          date: Date = Date(),
                          timeZone: TimeZone = TimeZone.getDefault()): List<MonthDisplay> {
        val thisMonth = DateHelper.firstDayOfMonth(date, timeZone)
        val year = thisMonth.get(Calendar.YEAR)
        val month = thisMonth.get(Calendar.MONTH)
        val date = thisMonth.get(Calendar.DATE)
        return (0..numOfMonths).map { i ->
            val newCalendar = GregorianCalendar()
            val newMonth = (month - i) % 12
            val newYear = if (newMonth > month) year - 1 else year
            newCalendar.set(newYear, newMonth, date)
            val monthName = newCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            val yearName = newCalendar.get(Calendar.YEAR)
            MonthDisplay("$monthName $yearName", newCalendar)
        }
    }
}