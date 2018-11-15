package me.jacoblewis.dailyexpense.mainActivity.interfaces

import android.support.v4.app.Fragment
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.openURI
import me.jacoblewis.dailyexpense.fragments.enterPayment.EnterPaymentFragment
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment

interface NavigationController : ActivityController {
    var currentRootFragment: RootFragment
    fun navigateTo(navScreen: NavScreen) {
        when (navScreen) {
            is NavScreen.Feedback -> openFeedback()
            is NavScreen.Settings -> openSettings()
            is NavScreen.EnterPayment -> enterPayment()
        }
    }

    fun enterPayment() {
        currentRootFragment = EnterPaymentFragment()
        currentActivity.supportFragmentManager.beginTransaction().replace(R.id.frame_root, currentRootFragment as Fragment).addToBackStack(null).commit()
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

}