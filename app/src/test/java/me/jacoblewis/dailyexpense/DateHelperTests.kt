package me.jacoblewis.dailyexpense

import me.jacoblewis.dailyexpense.commons.DateHelper
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class DateHelperTests {

    @Test
    fun testDaysLeftInDec16() {
        val date = Date(1545017609086L)
        assertEquals(16, DateHelper.daysLeftInMonth(date, TimeZone.getTimeZone("CST")))
    }

    @Test
    fun testDaysLeftInDec21() {
        val date = Date(1545415200000L)
        assertEquals(11, DateHelper.daysLeftInMonth(date, TimeZone.getTimeZone("CST")))
    }

    @Test
    fun testDaysLeftInFeb21() {
        val date = Date(1519236000000)
        assertEquals(8, DateHelper.daysLeftInMonth(date, TimeZone.getTimeZone("CST")))
    }

    @Test
    fun testFirstDayOfMonth() {
        val date = Date(1545415200000L)
        val calendar = DateHelper.firstDayOfMonth(date, TimeZone.getTimeZone("CST"))
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(11, calendar.get(Calendar.MONTH))
        assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, calendar.get(Calendar.MINUTE))
        assertEquals(0, calendar.get(Calendar.SECOND))
    }

    @Test
    fun testEndOfMonth() {
        val date = Date(1545415200000L)
        val cal = DateHelper.firstDayOfMonth(date, TimeZone.getTimeZone("CST"))
        val calendar = DateHelper.endOfMonth(cal)
        assertEquals(2018, calendar.get(Calendar.YEAR))
        assertEquals(11, calendar.get(Calendar.MONTH))
        assertEquals(31, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, calendar.get(Calendar.MINUTE))
        assertEquals(59, calendar.get(Calendar.SECOND))
    }

    @Test
    fun testEndOfMonth2() {
        val date = Date(1551419999211)
        val cal = DateHelper.firstDayOfMonth(date, TimeZone.getTimeZone("CST"))
        val calendar = DateHelper.endOfMonth(cal)
        assertEquals(2019, calendar.get(Calendar.YEAR))
        assertEquals(1, calendar.get(Calendar.MONTH))
        assertEquals(28, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, calendar.get(Calendar.MINUTE))
        assertEquals(59, calendar.get(Calendar.SECOND))
    }
}