package me.jacoblewis.dailyexpense.fragments.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.managers.BalanceManager
import java.util.*
import javax.inject.Inject

class MainViewModel
@Inject
constructor(val db: BalancesDB, val balanceManager: BalanceManager) : ViewModel() {
    private val currentDayOfMonth: MutableLiveData<Int> = MutableLiveData()
    val payments: LiveData<List<PaymentCategory>> = db.paymentsDao().getAllPaymentsSince(DateHelper.firstDayOfMonth(Date(), TimeZone.getDefault()))
    val dailyBalance: LiveData<Float> = Transformations.switchMap(currentDayOfMonth) {
        balanceManager.fetchDailyBalance()
    }

    init {
        currentDayOfMonth.value = DateHelper.dayOfMonth
    }

    fun checkCurrentDay() {
        if (currentDayOfMonth.value != DateHelper.dayOfMonth) {
            currentDayOfMonth.value = DateHelper.dayOfMonth
        }
    }
}