package me.jacoblewis.dailyexpense

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.data.PaymentsDao
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.managers.BalanceManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
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
    lateinit var mockedSP: SharedPreferences

    // Constants
    val timeZone = TimeZone.getTimeZone("CST")

    // Manager to test
    lateinit var balanceManager: BalanceManager

    @Before
    fun prepareTests() {
        Mockito.`when`(mockedSP.getString("budget", "500")).thenReturn("500")
        balanceManager = BalanceManager(mockedPaymentsDao, mockedSP)
    }

    @Test
    fun testDailyBalanceNoPayments() {
        val date = Date(1545017609086L) // Dec 16th, 2018 - CST
        val firstDayOfMonth = DateHelper.firstDayOfMonth(date, timeZone)
        Mockito.`when`(mockedPaymentsDao.getAllPaymentsSince(firstDayOfMonth)).thenReturn(MutableLiveData<List<PaymentCategory>>().apply {
            value = listOf()
        })

        val observer: Observer<Float> = Mockito.mock(getClazz())

        balanceManager.fetchDailyBalance(date, timeZone).observeForever(observer)

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

        balanceManager.fetchDailyBalance(date, timeZone).observeForever(observer)

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

        balanceManager.fetchDailyBalance(date, timeZone).observeForever(observer)

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

        balanceManager.fetchDailyBalance(date, timeZone).observeForever(observer)

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

        balanceManager.fetchDailyBalance(date, timeZone).observeForever(observer)

        // ($500-$10) / 16 days remaining. $10 payment on
        verify(observer).onChanged(20.625f)
        verifyNoMoreInteractions(observer)
    }

    private inline fun <reified T> getClazz(): Class<T> {
        return T::class.java
    }

    private fun getPaymentCats(vararg payments: Payment): List<PaymentCategory> {
        return payments.map { PaymentCategory().apply { transaction = it } }
    }
}