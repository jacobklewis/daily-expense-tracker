package me.jacoblewis.dailyexpense.commons

import me.jacoblewis.dailyexpense.data.models.Payment

object BudgetBalancer {

    fun calculateRemainingBudget(budget: Float, currentPayments: List<Payment>): Float {
        return budget - currentPayments.map { it.cost }.sum()
    }

    fun calculateRemainingMonthlyDailyBudget(remainingBudget: Float, remainingDaysInMonth: Int): Float {
        return remainingBudget / remainingDaysInMonth
    }

    fun calculateRemainingDailyBudget(remainingMonthlyDailyBudget: Float, todaysPayments: List<Payment>): Float {
        return remainingMonthlyDailyBudget - todaysPayments.map { it.cost }.sum()
    }
}