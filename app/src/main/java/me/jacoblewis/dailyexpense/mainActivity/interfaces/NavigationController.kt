package me.jacoblewis.dailyexpense.mainActivity.interfaces

import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootScreenFragment
import me.jacoblewis.dailyexpense.commons.openURI
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen

interface NavigationController : ActivityController {
    var currentRootFragment: RootScreenFragment
    fun navigateTo(navScreen: NavScreen) {
        when (navScreen) {
            is NavScreen.Feedback -> openFeedback()
            is NavScreen.Settings -> openSettings()
        }
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