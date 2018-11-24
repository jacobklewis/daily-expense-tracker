package me.jacoblewis.dailyexpense.mainActivity.interfaces

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import me.jacoblewis.dailyexpense.commons.ARG_PAYMENT
import me.jacoblewis.dailyexpense.fragments.categories.CategoryFragment
import me.jacoblewis.dailyexpense.fragments.categories.ChooseCategoryFragment
import me.jacoblewis.dailyexpense.fragments.enterCategory.EnterCategoryDialogFragment
import me.jacoblewis.dailyexpense.fragments.enterPayment.EnterPaymentFragment
import me.jacoblewis.dailyexpense.fragments.main.MainFragment
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import java.util.*

interface NavigationController : NavigationHandler {

    fun navigateTo(navScreen: NavScreen) {
        when (navScreen) {
            is NavScreen.Main -> openMain()
            is NavScreen.Settings -> openSettings()
            is NavScreen.EnterCategory -> enterCategory()
            is NavScreen.EnterPayment -> enterPayment(navScreen)
            is NavScreen.Categories -> openCategories()
            is NavScreen.ChooseCategory -> chooseCategory(navScreen)
        }
    }

    private fun openMain() {
        navTo(MainFragment(), addToBackStack = false, navUpTo = true)
    }

    private fun openCategories() {
        navTo(CategoryFragment(), addToBackStack = false, navUpTo = true)
    }

    private fun enterPayment(enterPayment: NavScreen.EnterPayment) {
        navTo(EnterPaymentFragment.createWithRevealAnimation(enterPayment.revealAnimationSetting))
    }


    private fun enterCategory() {
        val enterCategoryFrag = EnterCategoryDialogFragment()
        enterCategoryFrag.show(currentActivity.supportFragmentManager, EnterCategoryDialogFragment::class.java.name)
    }

    private fun chooseCategory(chooseCategory: NavScreen.ChooseCategory) {
        val chooseFrag = ChooseCategoryFragment()
        chooseFrag.arguments = Bundle().also { it.putParcelable(ARG_PAYMENT, chooseCategory.payment) }
        navTo(chooseFrag)
    }

//    private fun openFeedback() {
//        openURI(currentActivity, listOf(
//                "market://details?id=me.jacoblewis.bowlingscorekeeper",
//                "https://play.google.com/store/apps/details?me.jacoblewis.bowlingscorekeeper"
//        ))
//    }

    private fun openSettings() {
        TODO("Open Settings")
    }
}