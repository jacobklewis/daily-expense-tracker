package me.jacoblewis.dailyexpense.commons

sealed class StatsType {
    object Overview : StatsType()
    object NextDay : StatsType()
    object PieChart : StatsType()
    object PressureMeter : StatsType()
}