package me.jacoblewis.dailyexpense.mainActivity

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.mainActivity.interfaces.ExceptionController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.NavigationController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.PermissionController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.UpdateController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavUIUpdate
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(),
        NavigationController,
        ExceptionController,
        PermissionController,
        UpdateController,
        CoroutineScope {
    override val currentActivity: AppCompatActivity = this
    override val listeners: MutableMap<LifecycleOwner, (NavUIUpdate) -> Unit> = mutableMapOf()
    var actionBarToggle: ActionBarDrawerToggle? = null
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        setContentView(R.layout.activity_main)
        setupNav(savedInstanceState)
        setupDrawer()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancelChildren()
    }

    private fun setupDrawer() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_overview -> navigateTo(NavScreen.Main)
                R.id.menu_item_payments -> navigateTo(NavScreen.Payments)
                R.id.menu_item_categories -> navigateTo(NavScreen.Categories)
                R.id.menu_item_settings -> navigateTo(NavScreen.Settings)
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun onBackPressed() {
        handleOnBack { super.onBackPressed() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveNavState(outState)
        super.onSaveInstanceState(outState)
    }
}
