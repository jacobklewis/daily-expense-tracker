package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

import android.support.v4.app.Fragment

abstract class RootScreenFragment : Fragment() {
    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    abstract fun navigateBack(): Boolean
}