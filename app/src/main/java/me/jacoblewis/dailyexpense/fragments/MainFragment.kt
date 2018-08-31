package me.jacoblewis.dailyexpense.fragments

import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootScreenFragment

class MainFragment : RootScreenFragment() {

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        return false
    }
}