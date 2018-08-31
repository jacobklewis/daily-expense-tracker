package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

sealed class NavUIUpdate {
    object Refresh : NavUIUpdate()
}