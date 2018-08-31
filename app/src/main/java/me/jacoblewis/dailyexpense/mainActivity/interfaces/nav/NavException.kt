package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

sealed class NavException(message: String = "", cause: Throwable? = null) : Exception(message, cause) {

}