package me.jacoblewis.dailyexpense.fragments.settings

import android.content.Context
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.BudgetBalancer
import me.jacoblewis.dailyexpense.commons.PREFS_SETTINGS
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.mainActivity.interfaces.NavigationController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen

class SettingsUIFragment : PreferenceFragmentCompat() {

    lateinit var navigationController: NavigationController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigationController = context as NavigationController
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Set the shared preferences file name
        preferenceManager.sharedPreferencesName = PREFS_SETTINGS
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }


    override fun onBindPreferences() {
        // Change the summary
        findPreference<Preference>("budget").setSummaryProvider { BudgetBalancer.budgetFromSharedPrefs(preferenceManager.sharedPreferences).asCurrency }

        // Override Category preference and use our custom one instead
        findPreference<Preference>("edit_cats").onPreferenceClickListener = Preference.OnPreferenceClickListener {
            navigationController.navigateTo(NavScreen.EditCategory)
            return@OnPreferenceClickListener true
        }
    }
}