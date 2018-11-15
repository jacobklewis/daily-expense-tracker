package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

sealed class NavScreen {
    object Settings : NavScreen()
    object Feedback : NavScreen()
    object EnterPayment : NavScreen()
}