package me.jacoblewis.dailyexpense.mainActivity.interfaces

import android.os.Bundle
import me.jacoblewis.dailyexpense.commons.ARG_PAYMENT
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.extensions.applyModel
import me.jacoblewis.dailyexpense.fragments.categories.CategoryEditFragment
import me.jacoblewis.dailyexpense.fragments.categories.CategoryOverviewFragment
import me.jacoblewis.dailyexpense.fragments.categories.ChooseCategoryFragment
import me.jacoblewis.dailyexpense.fragments.enterCategory.EnterCategoryDialogFragment
import me.jacoblewis.dailyexpense.fragments.enterPayment.EnterPaymentFragment
import me.jacoblewis.dailyexpense.fragments.main.MainFragment
import me.jacoblewis.dailyexpense.fragments.payments.PaymentsFragment
import me.jacoblewis.dailyexpense.fragments.settings.SettingsFragment
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen

interface NavigationController : NavigationHandler {

    fun navigateTo(navScreen: NavScreen) {
        when (navScreen) {
            is NavScreen.Main -> openMain()
            is NavScreen.Settings -> openSettings()
            is NavScreen.EnterCategory -> enterCategory()
            is NavScreen.EditCategory -> editCategory(navScreen.category)
            is NavScreen.EnterPayment -> enterPayment(navScreen)
            is NavScreen.Categories -> openCategoriesOverview()
            is NavScreen.Payments -> openPaymentsOverview()
            is NavScreen.EditCategories -> openEditCategories()
            is NavScreen.ChooseCategory -> chooseCategory(navScreen)
        }
    }

    private fun openMain() {
        navTo(MainFragment(), addToBackStack = false, navUpTo = true)
    }

    private fun openCategoriesOverview() {
        navTo(CategoryOverviewFragment(), addToBackStack = false, navUpTo = true)
    }

    private fun openPaymentsOverview() {
        navTo(PaymentsFragment(), addToBackStack = false, navUpTo = true)
    }

    private fun openEditCategories() {
        navTo(CategoryEditFragment())
    }

    private fun enterPayment(enterPayment: NavScreen.EnterPayment) {
        navTo(EnterPaymentFragment.createWithRevealAnimation(enterPayment.revealAnimationSetting))
    }


    private fun enterCategory() {
        val enterCategoryFrag = EnterCategoryDialogFragment()
        enterCategoryFrag.show(currentActivity.supportFragmentManager, EnterCategoryDialogFragment::class.java.name)
    }

    private fun editCategory(category: Category) {
        val enterCategoryFrag = EnterCategoryDialogFragment()
        enterCategoryFrag.arguments = Bundle().applyModel { EnterCategoryDialogFragment.Config(category) }
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
        navTo(SettingsFragment(), addToBackStack = false, navUpTo = true)
    }
}