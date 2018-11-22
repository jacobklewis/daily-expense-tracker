package me.jacoblewis.dailyexpense.mainActivity

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.ExceptionController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.NavigationController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.PermissionController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.UpdateController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavUIUpdate

class MainActivity : AppCompatActivity(),
        NavigationController,
        ExceptionController,
        PermissionController,
        UpdateController {
    override val fragmentFrame: Int = R.id.frame_root
    override val currentActivity: AppCompatActivity = this
    override val listeners: MutableMap<LifecycleOwner, (NavUIUpdate) -> Unit> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyApp.graph.inject(this)
        setupNav(savedInstanceState)
    }

    override fun onBackPressed() {
        handleOnBack { super.onBackPressed() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveNavState(outState)
        super.onSaveInstanceState(outState)
    }
}
