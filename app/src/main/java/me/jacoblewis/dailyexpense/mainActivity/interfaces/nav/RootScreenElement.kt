package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

import android.support.v4.app.Fragment

interface RootScreenElement {
    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    fun navigateBack(): Boolean
}