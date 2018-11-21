package me.jacoblewis.dailyexpense.mainActivity.interfaces

import android.os.Bundle
import android.support.v4.app.Fragment
import me.jacoblewis.dailyexpense.commons.ARG_PAYMENT
import me.jacoblewis.dailyexpense.commons.openURI
import me.jacoblewis.dailyexpense.fragments.categories.ChooseCategoryFragment
import me.jacoblewis.dailyexpense.fragments.enterPayment.EnterPaymentFragment
import me.jacoblewis.dailyexpense.fragments.main.MainFragment
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import java.util.*

interface NavigationController : ActivityController {
    val fragmentFrame: Int

    companion object {
        val rootFragmentStack: Stack<RootFragment> by lazy { Stack<RootFragment>() }
    }

    fun navigateTo(navScreen: NavScreen, navBack: Boolean = false) {
        when (navScreen) {
            is NavScreen.Main -> openMain(navBack)
            is NavScreen.Feedback -> openFeedback()
            is NavScreen.Settings -> openSettings()
            is NavScreen.EnterPayment -> enterPayment(navScreen)
            is NavScreen.ChooseCategory -> chooseCategory(navScreen)
        }
    }

    private fun openMain(navBack: Boolean = false) {
        if (navBack) {
            while (rootFragmentStack.isNotEmpty() && rootFragmentStack.peek().screenTag != MainFragment::class.java.name) {
                rootFragmentStack.pop()
            }
            navRootUp(rootFragmentStack.peek(), addToBackStack = false)
        } else {
            navRootUp(MainFragment())
        }

    }

    private fun enterPayment(enterPayment: NavScreen.EnterPayment) {
        navRootUp(EnterPaymentFragment.createWithRevealAnimation(enterPayment.revealAnimationSetting))
    }

    private fun chooseCategory(chooseCategory: NavScreen.ChooseCategory) {
        val chooseFrag = ChooseCategoryFragment()
        chooseFrag.arguments = Bundle().also { it.putParcelable(ARG_PAYMENT, chooseCategory.payment) }
        navRootUp(chooseFrag)
    }

    private fun openFeedback() {
        openURI(currentActivity, listOf(
                "market://details?id=me.jacoblewis.bowlingscorekeeper",
                "https://play.google.com/store/apps/details?me.jacoblewis.bowlingscorekeeper"
        ))
    }

    private fun openSettings() {
        TODO("Open Settings")
    }

    // Helpers for navigation
    // ----------------------
    fun setupNav(savedInstanceState: Bundle?) {
        if (rootFragmentStack.empty()) {
            rootFragmentStack.push(loadFragment(MainFragment::class.java, savedInstanceState))
        }
        rootFragmentStack.peek() attachIfNeededTo this
    }

    fun saveNavState(outState: Bundle) {
        currentActivity.supportFragmentManager.putFragment(outState, RootFragment::class.java.name, rootFragmentStack.peek() as Fragment)
    }

    private fun navRootUp(fragment: RootFragment, addToBackStack: Boolean = true) {
        val currentRoot = rootFragmentStack.peek()
        if (addToBackStack) {
            rootFragmentStack.push(fragment)
        } else {
            if (!rootFragmentStack.empty()) {
                rootFragmentStack.pop()
            }
            rootFragmentStack.push(fragment)
        }
//        (currentRoot as Fragment).
        currentActivity.supportFragmentManager.beginTransaction().
//                setCustomAnimations(rootFragmentStack.peek().transitionIn, currentRoot.transitionOut).
                replace(fragmentFrame, rootFragmentStack.peek() as Fragment).commitNow()
    }

    fun handleOnBack(pressBack: () -> Unit) {
        val currentRoot = rootFragmentStack.pop()
        if (!currentRoot.navigateBack()) {
            if (rootFragmentStack.empty()) {
                pressBack()
            } else {
                currentActivity.supportFragmentManager.beginTransaction().
//                        setCustomAnimations(rootFragmentStack.peek().transitionIn, currentRoot.transitionOut).
                        replace(fragmentFrame, rootFragmentStack.peek() as Fragment).commitNow()
            }
        } else {
            // If an action was taken within the fragment, keep it on the stack
            rootFragmentStack.push(currentRoot)
        }
    }

    /*
    Get a T-Type Fragment from the fragment manager via it's tag
     */
    private fun <T : RootFragment> loadFragment(fragClass: Class<T>,
                                                instanceState: Bundle?): RootFragment {
        val currentFrag = currentActivity.supportFragmentManager.getFragment(instanceState
                ?: Bundle(), fragClass.name)
        return if (instanceState != null && currentFrag != null) {
            currentFrag as RootFragment
        } else {
            fragClass.newInstance()
        }
    }

    private infix fun RootFragment.attachIfNeededTo(navController: NavigationController) {
        if (navController.currentActivity.supportFragmentManager.findFragmentByTag(screenTag) == null) {
            currentActivity.supportFragmentManager.beginTransaction().replace(fragmentFrame, this as Fragment).commitNow()
        }
    }
}