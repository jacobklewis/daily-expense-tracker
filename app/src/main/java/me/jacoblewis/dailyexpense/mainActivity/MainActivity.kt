package me.jacoblewis.dailyexpense.mainActivity

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.fragments.main.MainFragment
import me.jacoblewis.dailyexpense.mainActivity.interfaces.ExceptionController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.NavigationController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.PermissionController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.UpdateController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavUIUpdate
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment

class MainActivity : AppCompatActivity(),
        NavigationController,
        ExceptionController,
        PermissionController,
        UpdateController {
    override lateinit var currentRootFragment: RootFragment
    override val currentActivity: AppCompatActivity = this
    override val listeners: MutableMap<LifecycleOwner, (NavUIUpdate) -> Unit> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyApp.graph.inject(this)

        currentRootFragment = loadFragment(
                tag = RootFragment::class.java.name,
                defaultFragment = MainFragment::class.java,
                instanceState = savedInstanceState)
        supportFragmentManager.beginTransaction().replace(R.id.frame_root, currentRootFragment as Fragment).commit()
    }

    override fun onBackPressed() {
        if (!currentRootFragment.navigateBack()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle?) {
        supportFragmentManager.putFragment(outState, RootFragment::class.java.name, currentRootFragment as Fragment)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    /*
        Get a T-Type Fragment from the fragment manager via it's tag
         */
    private fun <T : RootFragment> loadFragment(tag: String,
                                                defaultFragment: Class<T>,
                                                instanceState: Bundle?): RootFragment {
        val currentFrag = supportFragmentManager.getFragment(instanceState ?: Bundle(), tag)
        return if (instanceState != null && currentFrag != null) {
            currentFrag as RootFragment
        } else {
            defaultFragment.newInstance()
        }
    }
}
