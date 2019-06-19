package me.jacoblewis.dailyexpense.commons

import me.jacoblewis.dailyexpense.data.models.Payment
import kotlin.math.max

object BudgetBalancer {

    fun calculateRemainingBudget(budget: Float, currentPayments: List<Payment>): Float {
        return budget - currentPayments.map { it.cost }.sum()
    }

    fun calculateRemainingMonthlyDailyBudget(remainingBudget: Float, remainingDaysInMonth: Int): Float {
        return remainingBudget / remainingDaysInMonth
    }

    fun calculateRemainingMonthlyDailyBudget(totalBudget: Float, remainingBudget: Float, totalDaysInMonth: Int, remainingDaysInMonth: Int, distributionFactor: Double = Math.E): Float {
        val averagePerDay = totalBudget / totalDaysInMonth
        val currentTermLength = max(1, (remainingDaysInMonth / distributionFactor).toInt())
        val normalDayBudgetSum = averagePerDay * (remainingDaysInMonth - currentTermLength)
        val termBudgetSum = remainingBudget - normalDayBudgetSum
        return termBudgetSum / currentTermLength
    }

    fun calculateRemainingDailyBudget(remainingMonthlyDailyBudget: Float, todaysPayments: List<Payment>): Float {
        return remainingMonthlyDailyBudget - todaysPayments.map { it.cost }.sum()
    }
}