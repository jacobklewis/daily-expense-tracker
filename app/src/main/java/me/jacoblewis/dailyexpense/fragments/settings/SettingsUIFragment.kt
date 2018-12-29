package me.jacoblewis.dailyexpense.fragments.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.PREFS_SETTINGS

class SettingsUIFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Set the shared preferences file name
        preferenceManager.sharedPreferencesName = PREFS_SETTINGS
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }


    override fun onBindPreferences() {
        findPreference<Preference>("budget").setSummaryProvider { preferenceManager.sharedPreferences.getString("budget", "") }
    }
}