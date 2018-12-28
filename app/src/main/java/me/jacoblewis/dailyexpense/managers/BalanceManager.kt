package me.jacoblewis.dailyexpense.managers

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import me.jacoblewis.dailyexpense.commons.BudgetBalancer
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.commons.fromCurrency
import me.jacoblewis.dailyexpense.data.BalancesDB

class BalanceManager(val db: BalancesDB, val sp: SharedPreferences) {

    fun fetchDailyBalance(): LiveData<Float> = Transformations.map(db.paymentsDao().getAllPaymentsSince(DateHelper.firstDayOfMonth())) { currentPayments ->
        val budget = sp.getString("budget", "$0")?.fromCurrency ?: 0f
        val remainingBudget = BudgetBalancer.calculateRemainingBudget(budget, currentPayments.mapNotNull { it.transaction })
        return@map BudgetBalancer.calculateRemainingMonthlyDailyBudget(remainingBudget)
    }
}