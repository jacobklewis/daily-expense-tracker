package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

import me.jacoblewis.dailyexpense.commons.RevealAnimationSetting
import me.jacoblewis.dailyexpense.data.models.Payment

sealed class NavScreen {
    object Main : NavScreen()
    object Settings : NavScreen()
    object Categories : NavScreen()
    object EnterCategory : NavScreen()
    data class EnterPayment(val revealAnimationSetting: RevealAnimationSetting) : NavScreen()
    data class ChooseCategory(val payment: Payment) : NavScreen()
}