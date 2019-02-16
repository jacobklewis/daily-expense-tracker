package me.jacoblewis.dailyexpense.commons

sealed class StatsType {
    object Overview : StatsType()
    object PieChart : StatsType()
}