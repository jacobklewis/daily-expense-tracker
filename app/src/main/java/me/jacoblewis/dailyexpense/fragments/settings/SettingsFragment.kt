package me.jacoblewis.dailyexpense.fragments.settings

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_main_content.view.*
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment

class SettingsFragment : RootFragment(R.layout.fragment_settings_content) {
    override val options: RootFragmentOptions = RootFragmentOptions(SettingsFragment::class.java, drawerNavId = R.id.menu_item_settings)

    override fun onViewBound(view: View) {
        view.toolbar.title = "Settings"
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationController.linkToolBarToDrawer(view.toolbar)
    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        return false
    }
}