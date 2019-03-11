package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

import me.jacoblewis.dailyexpense.commons.RevealAnimationSetting
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment

sealed class NavScreen {
    object Main : NavScreen()
    object Settings : NavScreen()
    object Categories : NavScreen()
    object Payments : NavScreen()
    object EditCategories : NavScreen()
    object EnterCategory : NavScreen()
    data class EditCategory(val category: Category) : NavScreen()
    data class EnterPayment(val revealAnimationSetting: RevealAnimationSetting) : NavScreen()
    data class ChooseCategory(val payment: Payment) : NavScreen()
}