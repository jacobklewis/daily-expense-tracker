package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.commons.oCV
import me.jacoblewis.dailyexpense.mainActivity.interfaces.NavigationController
import javax.inject.Inject

abstract class RootFragment(private val layoutId: Int) : androidx.fragment.app.Fragment(), RootScreenElement {
    override val screenTag: String
        get() = options.screenTag

    abstract val options: RootFragmentOptions
    abstract fun onViewBound(view: View)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var navigationController: NavigationController
    fun setRootElevation(el: Float) {
        view?.elevation = el
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigationController = context as NavigationController
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return oCV(layoutId, container, this::onViewBound)
    }
}