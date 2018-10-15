package me.jacoblewis.dailyexpense.fragments

import android.support.v4.app.Fragment
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment

class MainFragment : Fragment(), RootFragment {

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        return false
    }
}