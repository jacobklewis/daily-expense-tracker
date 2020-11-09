package me.jacoblewis.dailyexpense.mainActivity.interfaces

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.extensions.applyModel
import me.jacoblewis.dailyexpense.fragments.categories.CategoryEditFragment
import me.jacoblewis.dailyexpense.fragments.enterCategory.EnterCategoryDialogFragment
import me.jacoblewis.dailyexpense.fragments.enterPayment.EnterPaymentFragmentDirections
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen

interface NavigationController : NavigationHandler {

    fun navigateTo(navScreen: NavScreen) {
        when (navScreen) {
            is NavScreen.Main -> openMain()
            is NavScreen.Settings -> openSettings()
            is NavScreen.EnterCategory -> enterCategory()
            is NavScreen.EditCategory -> editCategory(navScreen.category)
            is NavScreen.EnterPayment -> enterPayment()
            is NavScreen.Categories -> openCategoriesOverview()
            is NavScreen.Payments -> openPaymentsOverview()
            is NavScreen.EditCategories -> openEditCategories()
            is NavScreen.ChooseCategory -> chooseCategory(navScreen)
        }
    }

    private fun openMain() {
        navController.navigate(navDirections(R.id.mainFragment), navOptions {
            popUpTo(R.id.mainFragment) { inclusive = true }
        })
    }

    private fun openCategoriesOverview() {
        navController.navigate(navDirections(R.id.categoryOverviewFragment), navOptions {
            popUpTo(R.id.mainFragment) { inclusive = false }
        })
    }

    private fun openPaymentsOverview() {
        navController.navigate(navDirections(R.id.paymentsFragment), navOptions {
            popUpTo(R.id.mainFragment) { inclusive = false }
        })
    }

    private fun openEditCategories() {
        navTo(CategoryEditFragment())
    }

    private fun enterPayment() {
        navController.navigate(navDirections(R.id.enterPaymentFragment), navOptions {
            this.popUpTo = R.id.paymentsFragment
        })
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
        val directions = EnterPaymentFragmentDirections.actionEnterPaymentFragmentToChooseCategoryFragment(chooseCategory.payment)
        navController.navigate(directions)
    }

//    private fun openFeedback() {
//        openURI(currentActivity, listOf(
//                "market://details?id=me.jacoblewis.bowlingscorekeeper",
//                "https://play.google.com/store/apps/details?me.jacoblewis.bowlingscorekeeper"
//        ))
//    }

    private fun openSettings() {
        navController.navigate(R.id.settingsFragment)
    }

    private val navController: NavController
        get() = currentActivity.findNavController(R.id.nav_host_fragment)

    private fun navDirections(forId: Int): NavDirections = object : NavDirections {
        override fun getArguments(): Bundle = Bundle.EMPTY

        override fun getActionId(): Int = forId
    }
}