package me.jacoblewis.dailyexpense.mainActivity.interfaces

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import me.jacoblewis.dailyexpense.fragments.main.MainFragment
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import java.util.*

interface NavigationHandler : ActivityController {
    val fragmentFrame: Int
    fun linkToolBarToDrawer(toolbar: Toolbar)

    companion object {
        val rootFragmentStack: Stack<RootFragment> by lazy { Stack<RootFragment>() }
    }

    fun setupNav(savedInstanceState: Bundle?) {
        if (rootFragmentStack.empty()) {
            rootFragmentStack.push(loadFragment(MainFragment::class.java, savedInstanceState))
        }
        rootFragmentStack.peek() attachIfNeededTo this
    }

    fun saveNavState(outState: Bundle) {
        currentActivity.supportFragmentManager.putFragment(outState, RootFragment::class.java.name, rootFragmentStack.peek() as Fragment)
    }

    fun navTo(fragment: RootFragment, addToBackStack: Boolean = true, navUpTo: Boolean = false) {
        if (navUpTo) {
            navStackUpTo(fragment)
        }
        if (addToBackStack) {
            rootFragmentStack.push(fragment)
        } else if (rootFragmentStack.isEmpty() || rootFragmentStack.first().screenTag != fragment.screenTag) {
            if (!rootFragmentStack.empty()) {
                rootFragmentStack.pop()
            }
            rootFragmentStack.push(fragment)
        }
        currentActivity.supportFragmentManager.beginTransaction().
//                setCustomAnimations(rootFragmentStack.peek().transitionIn, currentRoot.transitionOut).
                replace(fragmentFrame, rootFragmentStack.peek() as Fragment).commitNow()
    }

    private fun navStackUpTo(fragment: RootFragment) {
        while (rootFragmentStack.isNotEmpty() && rootFragmentStack.peek().screenTag != fragment.screenTag) {
            rootFragmentStack.pop()
        }
    }

    fun handleOnBack(pressBack: () -> Unit) {
        val currentRoot = rootFragmentStack.pop()
        if (!currentRoot.navigateBack()) {
            if (rootFragmentStack.empty()) {
                pressBack()
            } else {
                currentActivity.supportFragmentManager.beginTransaction().
//                        setCustomAnimations(rootFragmentStack.peek().transitionIn, currentRoot.transitionOut).
                        replace(fragmentFrame, rootFragmentStack.peek() as Fragment).commitNow()
            }
        } else {
            // If an action was taken within the fragment, keep it on the stack
            rootFragmentStack.push(currentRoot)
        }
    }

    /*
    Get a T-Type Fragment from the fragment manager via it's tag
     */
    private fun <T : RootFragment> loadFragment(fragClass: Class<T>,
                                                instanceState: Bundle?): RootFragment {
        val currentFrag = currentActivity.supportFragmentManager.getFragment(instanceState
                ?: Bundle(), fragClass.name)
        return if (instanceState != null && currentFrag != null) {
            currentFrag as RootFragment
        } else {
            fragClass.newInstance()
        }
    }

    private infix fun RootFragment.attachIfNeededTo(activityController: ActivityController) {
        if (activityController.currentActivity.supportFragmentManager.findFragmentByTag(screenTag) == null) {
            currentActivity.supportFragmentManager.beginTransaction().replace(fragmentFrame, this as Fragment).commitNow()
        }
    }
}