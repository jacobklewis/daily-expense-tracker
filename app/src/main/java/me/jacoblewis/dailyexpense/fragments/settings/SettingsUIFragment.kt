package me.jacoblewis.dailyexpense.fragments.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import me.jacoblewis.dailyexpense.R

class SettingsUIFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onBindPreferences() {
        findPreference("budget").setSummaryProvider { preferenceManager.sharedPreferences.getString("budget", "") }
    }
}