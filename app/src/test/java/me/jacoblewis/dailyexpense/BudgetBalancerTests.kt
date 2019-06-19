package me.jacoblewis.dailyexpense

import me.jacoblewis.dailyexpense.commons.BudgetBalancer
import me.jacoblewis.dailyexpense.data.models.Payment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BudgetBalancerTests {

    @Test
    fun testRemainingBudgetNoPayments() {
        assertEquals(500f, BudgetBalancer.calculateRemainingBudget(500f, listOf()))
        assertEquals(100f, BudgetBalancer.calculateRemainingBudget(100f, listOf()))
    }

    @Test
    fun testRemainingBudgetSimplePayments() {
        assertEquals(485f, BudgetBalancer.calculateRemainingBudget(500f, listOf(Payment(5f), Payment(10f))))
    }

    @Test
    fun testRemainingBudgetComplexPayments() {
        assertEquals(370.03f, BudgetBalancer.calculateRemainingBudget(458.12f, listOf(Payment(5.02f), Payment(10.10f), Payment(56.84f), Payment(16.13f))))
    }

    // Daily Budget Tests
    @Test
    fun testRemainingMonthlyDailyBudget() {
        assertEquals(10f, BudgetBalancer.calculateRemainingMonthlyDailyBudget(160f, 16))
    }

    @Test
    fun testRemainingMonthlyDailyBudget2() {
        assertEquals(0.5f, BudgetBalancer.calculateRemainingMonthlyDailyBudget(10f, 20))
    }

    @Test
    fun testRemainingMonthlyDailyBudgetAssisted() {
        assertTrue(Math.abs(25f - BudgetBalancer.calculateRemainingMonthlyDailyBudget(100f, 80f, 10, 5, distributionFactor = 2.0)) < 0.01f)
    }

    @Test
    fun testRemainingMonthlyDailyBudgetAssisted2() {
        val res = BudgetBalancer.calculateRemainingMonthlyDailyBudget(750f, 600f, 30, 23, distributionFactor = 2.0)
        assertTrue(27.273f - res < 0.01f)
    }

    @Test
    fun testRemainingMonthlyDailyBudgetAssisted3() {
        val res = BudgetBalancer.calculateRemainingMonthlyDailyBudget(750f, 50f, 30, 1, distributionFactor = 2.0)
        assertTrue(50f - res < 0.01f)
    }

    @Test
    fun testRemainingMonthlyDailyBudgetAssisted4() {
        val res = BudgetBalancer.calculateRemainingMonthlyDailyBudget(750f, 750f, 30, 30, distributionFactor = 2.0)
        assertTrue(25f - res < 0.01f)
    }

    @Test
    fun testRemainingMonthlyDailyBudgetAssisted5() {
        val res = BudgetBalancer.calculateRemainingMonthlyDailyBudget(750f, 750f, 29, 29, distributionFactor = 2.0)
        assertTrue(25.8621f - res < 0.01f)
    }

    // Daily Budget Tests
    @Test
    fun testRemainingDailyBudgetNoPayments() {
        assertEquals(10f, BudgetBalancer.calculateRemainingDailyBudget(10f, listOf()))
    }

    @Test
    fun testRemainingDailyBudgetSomePayments() {
        assertEquals(5f, BudgetBalancer.calculateRemainingDailyBudget(10f, listOf(Payment(2f), Payment(3f))))
    }


}