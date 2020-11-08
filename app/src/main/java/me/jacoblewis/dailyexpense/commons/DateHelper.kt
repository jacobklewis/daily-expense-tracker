package me.jacoblewis.dailyexpense.commons

import java.util.*

object DateHelper {

    fun daysLeftInMonth(date: Date, timezone: TimeZone): Int {
        val calendar = GregorianCalendar(timezone)
        calendar.time = date
        val daysInMonth = daysInMonth(date, timezone)
        return daysInMonth - calendar.get(Calendar.DAY_OF_MONTH) + 1
    }

    fun daysInMonth(date: Date, timezone: TimeZone): Int {
        val calendar = GregorianCalendar(timezone)
        calendar.time = date
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
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

    fun endOfMonth(calendar: Calendar): Calendar {
        val newCalendar = GregorianCalendar()
        newCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        newCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        newCalendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        newCalendar.set(Calendar.HOUR_OF_DAY, 23)
        newCalendar.set(Calendar.MINUTE, 59)
        newCalendar.set(Calendar.SECOND, 59)
        return newCalendar
    }

    fun today(date: Date, timezone: TimeZone): Calendar {
        val calendar = GregorianCalendar(timezone)
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar
    }

    fun daysAhead(date: Date, timezone: TimeZone, numOfDays: Int): Calendar {
        val calendar = GregorianCalendar(timezone)
        calendar.time = date
        calendar.add(Calendar.HOUR, numOfDays * 24)
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