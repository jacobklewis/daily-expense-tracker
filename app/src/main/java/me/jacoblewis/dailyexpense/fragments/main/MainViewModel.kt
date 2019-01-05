package me.jacoblewis.dailyexpense.fragments.main

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import me.jacoblewis.dailyexpense.commons.BudgetBalancer
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.managers.BalanceManager
import java.util.*
import javax.inject.Inject

class MainViewModel
@Inject
constructor(val db: BalancesDB, val balanceManager: BalanceManager, val sp: SharedPreferences) : ViewModel() {
    private val currentDayOfMonth: MutableLiveData<Int> = MutableLiveData()
    val payments: LiveData<Pair<List<PaymentCategory>, Float>> = Transformations.map(db.paymentsDao().getAllPaymentsSince(DateHelper.firstDayOfMonth(Date(), TimeZone.getDefault())))
    {
        Pair(it, BudgetBalancer.calculateRemainingBudget(BudgetBalancer.budgetFromSharedPrefs(sp), it.mapNotNull { p -> p.transaction }))
    }

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