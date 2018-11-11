package me.jacoblewis.dailyexpense.fragments

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.AppBarStateChangeListener
import me.jacoblewis.dailyexpense.commons.State
import me.jacoblewis.dailyexpense.commons.addStateChangeListener
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment

class MainFragment : Fragment(), RootFragment {

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.drawer_layout)
    lateinit var drawerLayout: DrawerLayout

    @BindView(R.id.navigation_view)
    lateinit var navView: NavigationView

    @BindView(R.id.main_appbar)
    lateinit var appBarLayout: AppBarLayout

    @BindView(R.id.main_collapsing)
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        ButterKnife.bind(this, rootView)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.title_open_nav, R.string.title_close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setCheckedItem(R.id.menu_item_overview)
        navView.setNavigationItemSelectedListener {
            return@setNavigationItemSelectedListener true
        }

        val money = "$203.50"

        appBarLayout.addStateChangeListener { _, state ->
            collapsingToolbarLayout.title = when (state) {
                State.COLLAPSED -> "$money - Daily Balance"
                else -> money
            }
        }

        return rootView
    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
        return false
    }
}