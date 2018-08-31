package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

sealed class NavPermission {
    data class Storage(val force: Boolean = false, val result: (Boolean) -> Unit) : NavPermission()
}