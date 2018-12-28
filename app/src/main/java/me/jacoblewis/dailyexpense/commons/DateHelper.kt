package me.jacoblewis.dailyexpense.commons

import java.util.*

object DateHelper {

    fun daysLeftInMonth(date: Date = Date(), timezone: TimeZone = TimeZone.getDefault()): Int {
        val calendar = GregorianCalendar(timezone)
        calendar.time = date
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        return daysInMonth - calendar.get(Calendar.DAY_OF_MONTH) + 1
    }

    fun firstDayOfMonth(date: Date = Date(), timezone: TimeZone = TimeZone.getDefault()): Calendar {
        val calendar = GregorianCalendar(timezone)
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar
    }
}