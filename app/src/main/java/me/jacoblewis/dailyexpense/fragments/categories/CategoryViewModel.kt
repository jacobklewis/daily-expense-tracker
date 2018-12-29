package me.jacoblewis.dailyexpense.fragments.categories

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.commons.BudgetBalancer
import me.jacoblewis.dailyexpense.data.CategoriesDao
import me.jacoblewis.dailyexpense.data.PaymentsDao
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment
import java.util.*
import javax.inject.Inject

class CategoryViewModel
@Inject
constructor(val categoriesDao: CategoriesDao, val paymentsDao: PaymentsDao, val sp: SharedPreferences) : ViewModel() {

    private val fromDate: MutableLiveData<Calendar> = MutableLiveData()
    val categories: LiveData<List<Category>> = Transformations.switchMap(fromDate) { lowerDate ->
        Transformations.map(categoriesDao.getAllCategoryPayments()) { cats ->
            return@map cats.map { categoryPayments ->
                val thisMonthsPayments = categoryPayments.payments.filter { payment -> payment.creationDate > lowerDate }
                categoryPayments.category.apply {
                    payments.addAll(thisMonthsPayments)
                }
            }
        }
    }
    val budget = BudgetBalancer.budgetFromSharedPrefs(sp)

    fun updateCategoryDate(calendar: Calendar) {
        fromDate.value = calendar
    }

    fun updateCategories(categories: List<Category>) {
        GlobalScope.launch {
            categoriesDao.updateCategories(categories)
        }
    }

    fun savePayment(payment: Payment) {
        GlobalScope.launch {
            paymentsDao.insertPayment(payment)
        }
    }

    fun removeCategory(category: Category) {
        GlobalScope.launch {
            try {
                paymentsDao.deleteByCategory(categoryId = category.categoryId)
                categoriesDao.deleteCategory(category)
            } catch (e: Exception) {
                // TODO: log somewhere else
                Log.e("CategoryViewModel", e.localizedMessage)
            }
        }
    }
}