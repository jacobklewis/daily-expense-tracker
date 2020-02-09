package me.jacoblewis.dailyexpense.managers

import androidx.annotation.IntRange
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.data.daos.BudgetsDao
import me.jacoblewis.dailyexpense.data.daos.PaymentsDao
import me.jacoblewis.dailyexpense.data.models.Budget
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class BalanceManagerTests {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockedPaymentsDao: PaymentsDao
    @Mock
    lateinit var mockedBudgetsDao: BudgetsDao

    // Constants
    val timeZone = TimeZone.getTimeZone("CST")

    // Manager to test
    lateinit var balanceManager: BalanceManager

    @Before
    fun prepareTests() {
        Mockito.`when`(mockedBudgetsDao.getBudgetForMonth(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(Budget(500f, 0, 0))
    }

    @Test
    fun testDailyBalanceNoPayments() {
        val date = Date(1545017609086L) // Dec 16th, 2018 - CST
        val firstDayOfMonth = DateHelper.firstDayOfMonth(date, timeZone)
        Mockito.`when`(mockedPaymentsDao.getAllPaymentsSince(firstDayOfMonth)).thenReturn(MutableLiveData<List<PaymentCategory>>().apply {
            value = listOf()
        })

        val observer: Observer<Float> = Mockito.mock(getClazz())

        balanceManager = BalanceManager(mockedPaymentsDao, mockedBudgetsDao, date, timeZone, distributionFactor = 1.0)
        balanceManager.fetchDailyBalance().observeForever(observer)

        // $500 / 16 days remaining. No payments
        verify(observer).onChanged(31.25f)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun testDailyBalanceOnePaymentBefore() {
        val date = Date(1545017609086L) // Dec 16th, 2018 - CST
        val firstDayOfMonth = DateHelper.firstDayOfMonth(date, timeZone)
        val dayBefore = DateHelper.today(date, timeZone).apply { set(Calendar.DAY_OF_MONTH, 15) } // Dec 15th, 2018
        Mockito.`when`(mockedPaymentsDao.getAllPaymentsSince(firstDayOfMonth)).thenReturn(MutableLiveData<List<PaymentCategory>>().apply {
            value = getPaymentCats(Payment(50f, dayBefore))
        })

        val observer: Observer<Float> = Mockito.mock(getClazz())

        balanceManager = BalanceManager(mockedPaymentsDao, mockedBudgetsDao, date, timeZone, distributionFactor = 1.0)
        balanceManager.fetchDailyBalance().observeForever(observer)

        // ($500-$50) / 16 days remaining.
        verify(observer).onChanged(28.125f)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun testDailyBalanceOnePaymentAfter() {
        val date = Date(1545017609086L) // Dec 16th, 2018 - CST
        val firstDayOfMonth = DateHelper.firstDayOfMonth(date, timeZone)
        val dayAfter = DateHelper.today(date, timeZone).apply { set(Calendar.DAY_OF_MONTH, 17) } // Dec 17th, 2018
        Mockito.`when`(mockedPaymentsDao.getAllPaymentsSince(firstDayOfMonth)).thenReturn(MutableLiveData<List<PaymentCategory>>().apply {
            value = getPaymentCats(Payment(100f, dayAfter))
        })

        val observer: Observer<Float> = Mockito.mock(getClazz())

        balanceManager = BalanceManager(mockedPaymentsDao, mockedBudgetsDao, date, timeZone, distributionFactor = 1.0)
        balanceManager.fetchDailyBalance().observeForever(observer)

        // ($500-$100) / 16 days remaining. Each day is independent
        verify(observer).onChanged(25f)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun testDailyBalanceTwoPaymentsOn() {
        val date = Date(1545017609086L) // Dec 16th, 2018 - CST
        val firstDayOfMonth = DateHelper.firstDayOfMonth(date, timeZone)
        val dayOn1 = DateHelper.today(date, timeZone) // Dec 16th, 2018 (Hour 0)
        val dayOn2 = DateHelper.today(date, timeZone).apply { set(Calendar.HOUR_OF_DAY, 23) } // Dec 16th, 2018 (Hour 23)
        Mockito.`when`(mockedPaymentsDao.getAllPaymentsSince(firstDayOfMonth)).thenReturn(MutableLiveData<List<PaymentCategory>>().apply {
            value = getPaymentCats(Payment(10f, dayOn1), Payment(15f, dayOn2))
        })

        val observer: Observer<Float> = Mockito.mock(getClazz())

        balanceManager = BalanceManager(mockedPaymentsDao, mockedBudgetsDao, date, timeZone, distributionFactor = 1.0)
        balanceManager.fetchDailyBalance().observeForever(observer)

        // $500 / 16 days remaining. $25 payment on
        verify(observer).onChanged(6.25f)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun testDailyBalanceOneBeforeOneOn() {
        val date = Date(1545017609086L) // Dec 16th, 2018 - CST
        val firstDayOfMonth = DateHelper.firstDayOfMonth(date, timeZone)
        val dayBefore = DateHelper.today(date, timeZone).apply { set(Calendar.DAY_OF_MONTH, 15) } // Dec 15th, 2018
        val dayOn = DateHelper.today(date, timeZone) // Dec 16th, 2018
        Mockito.`when`(mockedPaymentsDao.getAllPaymentsSince(firstDayOfMonth)).thenReturn(MutableLiveData<List<PaymentCategory>>().apply {
            value = getPaymentCats(Payment(10f, dayBefore), Payment(10f, dayOn))
        })

        val observer: Observer<Float> = Mockito.mock(getClazz())

        balanceManager = BalanceManager(mockedPaymentsDao, mockedBudgetsDao, date, timeZone, distributionFactor = 1.0)
        balanceManager.fetchDailyBalance().observeForever(observer)

        // ($500-$10) / 16 days remaining. $10 payment on
        verify(observer).onChanged(20.625f)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun testBudgetPressure() {
        val date = Date(1545017609086L) // Dec 16th, 2018 - CST
        val firstDayOfMonth = DateHelper.firstDayOfMonth(date, timeZone)
        val dayBefore = DateHelper.today(date, timeZone).apply { set(Calendar.DAY_OF_MONTH, 15) } // Dec 15th, 2018
        val dayOn = DateHelper.today(date, timeZone) // Dec 16th, 2018
        Mockito.`when`(mockedPaymentsDao.getAllPaymentsSince(firstDayOfMonth)).thenReturn(MutableLiveData<List<PaymentCategory>>().apply {
            value = getPaymentCats(Payment(10f, dayBefore), Payment(10f, dayOn))
        })
        balanceManager = BalanceManager(mockedPaymentsDao, mockedBudgetsDao, date, timeZone, distributionFactor = 1.0)


        val pressure = balanceManager.currentBudgetPressure
//        19 december 2020 at (11 h 30 m 0)
//        19 december 2020 at "11:30:00.000"

    }

    infix fun Int.december(year: Int): Date {
        return Date()
    }

    infix fun Date.at(dayTime: DayTime): Date {
        return this
    }


    infix fun Date.at(timeStr: String): Date {
        return this
    }

    infix fun Int.h(minute: Int): DayTime {
        return DayTime(this, minute)
    }
    infix fun DayTime.m(second: Int): DayTime {
        this.second = second
        return this
    }

    infix fun Date.atHour(hour: Int): Date {
        return this
    }

    infix fun Date.atMinute(minute: Int): Date {
        return this
    }

    infix fun Date.atSecond(second: Int): Date {
        return this
    }

    infix fun Date.atMillisecond(millisecond: Int): Date {
        return this
    }

    data class DayTime(@IntRange(from = 0, to = 23) val hour: Int,
                       @IntRange(from = 0, to = 59) val minute: Int,
                       @IntRange(from = 0, to = 59) var second: Int = 0,
                       @IntRange(from = 0, to = 999) var millisecond: Int = 0)

    private inline fun <reified T> getClazz(): Class<T> {
        return T::class.java
    }

    private fun getPaymentCats(vararg payments: Payment): List<PaymentCategory> {
        return payments.map { PaymentCategory().apply { transaction = it } }
    }
}