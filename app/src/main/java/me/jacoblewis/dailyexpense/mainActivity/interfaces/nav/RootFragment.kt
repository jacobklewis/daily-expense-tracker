package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.commons.oCV
import me.jacoblewis.dailyexpense.mainActivity.interfaces.NavigationController

abstract class RootFragment(private val layoutId: Int) : androidx.fragment.app.Fragment(), RootScreenElement {
    override val screenTag: String
        get() = options.screenTag

    abstract val options: RootFragmentOptions
    abstract fun onViewBound(view: View)

    lateinit var navigationController: NavigationController
    lateinit var coroutineScope: CoroutineScope
    fun setRootElevation(el: Float) {
        view?.elevation = el
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigationController = context as NavigationController
        coroutineScope = context as CoroutineScope
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return oCV(layoutId, container, this::onViewBound)
    }
}