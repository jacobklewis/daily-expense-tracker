package me.jacoblewis.dailyexpense.mainActivity

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.fragments.MainFragment
import me.jacoblewis.dailyexpense.mainActivity.interfaces.ExceptionController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.NavigationController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.PermissionController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.UpdateController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavUIUpdate
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootScreenFragment

class MainActivity : AppCompatActivity(),
        NavigationController,
        ExceptionController,
        PermissionController,
        UpdateController {
    override lateinit var currentRootFragment: RootScreenFragment
    override val currentActivity: AppCompatActivity = this
    override val listeners: MutableMap<LifecycleOwner, (NavUIUpdate) -> Unit> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyApp.graph.inject(this)

        currentRootFragment = loadFragment(
                tag = RootScreenFragment::class.java.name,
                defaultFragment = MainFragment::class.java,
                instanceState = savedInstanceState)
        supportFragmentManager.beginTransaction().replace(R.id.frame_root, currentRootFragment).commit()
    }

    override fun onBackPressed() {
        if (!currentRootFragment.navigateBack()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle?) {
        supportFragmentManager.putFragment(outState, RootScreenFragment::class.java.name, currentRootFragment)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    /*
        Get a T-Type Fragment from the fragment manager via it's tag
         */
    private fun <T : RootScreenFragment> loadFragment(tag: String,
                                                      defaultFragment: Class<T>,
                                                      instanceState: Bundle?): RootScreenFragment {
        val currentFrag = supportFragmentManager.getFragment(instanceState ?: Bundle(), tag)
        return if (instanceState != null && currentFrag != null) {
            currentFrag as RootScreenFragment
        } else {
            defaultFragment.newInstance()
        }
    }
}
