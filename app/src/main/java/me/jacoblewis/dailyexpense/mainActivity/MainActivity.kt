package me.jacoblewis.dailyexpense.mainActivity

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.LifecycleOwner
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.navigation.NavigationView
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.*
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavUIUpdate

class MainActivity : AppCompatActivity(),
        NavigationController,
        ExceptionController,
        PermissionController,
        UpdateController {
    override val fragmentFrame: Int = R.id.frame_root
    override val currentActivity: AppCompatActivity = this
    override val listeners: MutableMap<LifecycleOwner, (NavUIUpdate) -> Unit> = mutableMapOf()
    var actionBarToggle: ActionBarDrawerToggle? = null

    @BindView(R.id.drawer_layout)
    lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    @BindView(R.id.navigation_view)
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        MyApp.graph.inject(this)
        setupNav(savedInstanceState)
        setupDrawer()
    }

    private fun setupDrawer() {

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_overview -> navigateTo(NavScreen.Main)
                R.id.menu_item_categories -> navigateTo(NavScreen.Categories)
                R.id.menu_item_settings -> navigateTo(NavScreen.Settings)
            }
            return@setNavigationItemSelectedListener true
        }
    }

    override fun linkToolBarToDrawer(toolbar: Toolbar) {
        val navId = NavigationHandler.rootFragmentStack.peek().options.drawerNavId
        if (navId != 0) {
            navView.setCheckedItem(navId)
        }
        val toggle = actionBarToggle?.also {
            drawerLayout.removeDrawerListener(it)
        } ?: ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.title_open_nav, R.string.title_close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        handleOnBack { super.onBackPressed() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveNavState(outState)
        super.onSaveInstanceState(outState)
    }
}
