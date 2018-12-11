package me.jacoblewis.dailyexpense.fragments.settings

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment

class SettingsFragment : RootFragment(R.layout.fragment_settings_content) {
    override val options: RootFragmentOptions = RootFragmentOptions(SettingsFragment::class.java, drawerNavId = R.id.menu_item_settings)

    init {
        MyApp.graph.inject(this)
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    override fun onViewBound(view: View) {
        toolbar.title = "Settings"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationController.linkToolBarToDrawer(toolbar)
    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        return false
    }
}