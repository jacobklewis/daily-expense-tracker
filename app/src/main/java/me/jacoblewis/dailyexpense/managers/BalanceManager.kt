package me.jacoblewis.dailyexpense.managers

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*
import me.jacoblewis.dailyexpense.commons.BudgetBalancer
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.commons.and
import me.jacoblewis.dailyexpense.data.daos.BudgetsDao
import me.jacoblewis.dailyexpense.data.daos.PaymentsDao
import me.jacoblewis.dailyexpense.data.models.Budget
import me.jacoblewis.dailyexpense.data.models.NextDay
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import java.util.*
import kotlin.math.min

class BalanceManager(val paymentsDao: PaymentsDao, private val budgetsDao: BudgetsDao, val date: Date? = null, private val timeZone: TimeZone = TimeZone.getDefault(), private val distributionFactor: Double = Math.E) {

    internal var cachedBudget: Budget? = null

    private val firstDayOfMonth: Calendar
        get() = DateHelper.firstDayOfMonth(date ?: Date(), timeZone)

    private val today: Calendar
        get() = DateHelper.today(date ?: Date(), timeZone)

    fun fetchDailyBalance(): LiveData<Float> = Transformations.map(paymentsDao.getAllPaymentsSince(firstDayOfMonth)) { currentPayments ->
        return@map processDailyBalance(currentPayments)
    }

    fun fetchNextDayBalance(): LiveData<List<NextDay>> = Transformations.map(paymentsDao.getAllPaymentsSince(firstDayOfMonth)) { currentPayments ->
        val nextDays = min(DateHelper.daysLeftInMonth(date ?: Date(), timeZone), 4)
        return@map (1..nextDays).map {
            val date = DateHelper.daysAhead(date
                    ?: Date(), timeZone, numOfDays = it)
            NextDay(date.time, processDailyBalance(currentPayments, date))
        }
    }

    @WorkerThread
    fun fetchDailyBalanceNow(): Float {
        val allPaymentsThisMonth = paymentsDao.getAllPaymentsSinceNow(firstDayOfMonth)
        return processDailyBalance(allPaymentsThisMonth)
    }

    private fun processDailyBalance(currentPayments: List<PaymentCategory>, today: Calendar = DateHelper.today(date
            ?: Date(), timeZone)): Float {
        val monthlyDailyBudget = calcTodaysBudget(currentPayments, today)

        // Get today's Payments
        val todaysPayments = currentPayments.mapNotNull { it.transaction }.filter { it.creationDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) }
        // Factor in Today's Payments
        return BudgetBalancer.calculateRemainingDailyBudget(monthlyDailyBudget, todaysPayments)
    }

    private fun calcTodaysBudget(currentPayments: List<PaymentCategory>, today: Calendar): Float {
        // Calculate Remaining budget
        val remainingBudget = BudgetBalancer.calculateRemainingBudget(currentBudget, currentPayments.mapNotNull { it.transaction }.filter { it.creationDate.get(Calendar.DAY_OF_MONTH) != today.get(Calendar.DAY_OF_MONTH) })
        // Calculate Average Remaining daily budget
        return BudgetBalancer.calculateRemainingMonthlyDailyBudget(currentBudget, remainingBudget, DateHelper.daysInMonth(today.time, timeZone), DateHelper.daysLeftInMonth(today.time, timeZone), distributionFactor)
    }

    var currentBudget: Float
        get() {
            val today: Calendar = DateHelper.today(date ?: Date(), timeZone)
            val budget = cachedBudget?.and { it.month == today.get(Calendar.MONTH) }
                    ?: runBlocking {
                        Log.e("Balance Manager", "Pulling Balance from DB")
                        getBudgetFromDB(today).also { cachedBudget = it }
                    }
            return budget.amount
        }
        set(value) {
            cachedBudget?.amount = value
            GlobalScope.launch {
                val today: Calendar = DateHelper.today(date ?: Date(), timeZone)
                val updatedBudget = budgetsDao.getBudgetForMonth(today.get(Calendar.YEAR), today.get(Calendar.MONTH))?.also {
                    it.amount = value
                } ?: Budget(value, today.get(Calendar.YEAR), today.get(Calendar.MONTH))
                budgetsDao.updateBudget(updatedBudget)
            }
        }

    /**
     * @return pressure from current usage [0..1]
     */
    fun fetchBudgetPressure(): LiveData<Float> = Transformations.map(paymentsDao.getAllPaymentsBetween(firstDayOfMonth, today)) { currentPayments ->
        return@map processBudgetPressure(currentPayments)
    }

    internal fun processBudgetPressure(currentPayments: List<PaymentCategory>): Float {
        val daysInMonth = DateHelper.daysInMonth(today.time, timeZone)
        val standardDailyBudget = currentBudget / daysInMonth
        println(standardDailyBudget)
        val currentDailyBudget = calcTodaysBudget(currentPayments, today)
        println(currentDailyBudget)

        return currentDailyBudget / (standardDailyBudget * 2)
    }

    /**
     * Get this month's budget from the Room DB
     */
    private suspend fun getBudgetFromDB(today: Calendar): Budget = withContext(Dispatchers.IO) {
        budgetsDao.getBudgetForMonth(today.get(Calendar.YEAR), today.get(Calendar.MONTH))
                ?: createBudgetForThisMonth(today)
    }

    /**
     * Create this month's budget and save it to the Room DB
     */
    @WorkerThread
    private fun createBudgetForThisMonth(today: Calendar) = run {
        // Create New Budget for this month if one does not yet exist
        // Use last month's budget for this month's budget
        val lastBudget = budgetsDao.getLastBudget()
        val newBudget = Budget(lastBudget?.amount
                ?: 500f, today.get(Calendar.YEAR), today.get(Calendar.MONTH))
        budgetsDao.insertBudget(newBudget)
        newBudget
    }
}