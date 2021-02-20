package me.jacoblewis.dailyexpense.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.commons.BudgetBalancer
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.data.daos.PaymentsDao
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.managers.BalanceManager
import java.util.*

class PaymentViewModel(val paymentsDao: PaymentsDao, val balanceManager: BalanceManager) : ViewModel() {
    private val currentDayOfMonth: MutableLiveData<Int> = MutableLiveData()
    val payments: LiveData<Pair<List<PaymentCategory>, Float>> = Transformations.map(paymentsDao.getAllPaymentsSince(DateHelper.firstDayOfMonth(Date(), TimeZone.getDefault())))
    {
        Pair(it, BudgetBalancer.calculateRemainingBudget(balanceManager.currentBudget, it.mapNotNull { p -> p.transaction }))
    }

    val dailyBalance: LiveData<Float> = Transformations.switchMap(currentDayOfMonth) {
        balanceManager.fetchDailyBalance()
    }
    val dailyPressure: LiveData<Float> = Transformations.switchMap(currentDayOfMonth) {
        balanceManager.fetchBudgetPressure()
    }

    init {
        currentDayOfMonth.value = DateHelper.dayOfMonth
    }

    fun checkCurrentDay() {
        if (currentDayOfMonth.value != DateHelper.dayOfMonth) {
            currentDayOfMonth.value = DateHelper.dayOfMonth
        }
    }

    fun removePayment(scope: CoroutineScope, payment: Payment) {
        scope.launch(context = Dispatchers.IO) {
            try {
                paymentsDao.deletePayment(payment)
            } catch (e: Exception) {
                // TODO: log somewhere else
                Log.e("PaymentViewModel", e.localizedMessage)
            }
        }
    }
}