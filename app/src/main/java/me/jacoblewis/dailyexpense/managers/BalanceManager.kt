package me.jacoblewis.dailyexpense.managers

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import me.jacoblewis.dailyexpense.commons.BudgetBalancer
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.data.PaymentsDao
import java.util.*

class BalanceManager(val paymentsDao: PaymentsDao, val sp: SharedPreferences) {

    fun fetchDailyBalance(date: Date = Date(), timeZone: TimeZone = TimeZone.getDefault()): LiveData<Float> = Transformations.map(paymentsDao.getAllPaymentsSince(DateHelper.firstDayOfMonth(date, timeZone))) { currentPayments ->
        val today: Calendar = DateHelper.today(date, timeZone)
        // Get Budget from Shared Preferences
        val budget = BudgetBalancer.budgetFromSharedPrefs(sp)
        // Calculate Remaining budget
        val remainingBudget = BudgetBalancer.calculateRemainingBudget(budget, currentPayments.mapNotNull { it.transaction }.filter { it.creationDate.get(Calendar.DAY_OF_MONTH) != today.get(Calendar.DAY_OF_MONTH) })
        // Calculate Average Remaining daily budget
        val monthlyDailyBudget = BudgetBalancer.calculateRemainingMonthlyDailyBudget(remainingBudget, DateHelper.daysLeftInMonth(date, timeZone))
        // Get today's Payments
        val todaysPayments = currentPayments.mapNotNull { it.transaction }.filter { it.creationDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) }
        // Factor in Today's Payments
        return@map BudgetBalancer.calculateRemainingDailyBudget(monthlyDailyBudget, todaysPayments)
    }
}