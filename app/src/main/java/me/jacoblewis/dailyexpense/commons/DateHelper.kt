package me.jacoblewis.dailyexpense.commons

import java.util.*

object DateHelper {

    fun daysLeftInMonth(date: Date, timezone: TimeZone): Int {
        val calendar = GregorianCalendar(timezone)
        calendar.time = date
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        return daysInMonth - calendar.get(Calendar.DAY_OF_MONTH) + 1
    }

    fun firstDayOfMonth(date: Date, timezone: TimeZone): Calendar {
        val calendar = GregorianCalendar(timezone)
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar
    }

    fun today(date: Date, timezone: TimeZone): Calendar {
        val calendar = GregorianCalendar(timezone)
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar
    }

    val dayOfMonth: Int
        get() = today(Date(), TimeZone.getDefault()).get(Calendar.DAY_OF_MONTH)

    val beginningOfTime: Calendar
        get() = today(Date(0), TimeZone.getDefault())
}