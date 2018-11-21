package me.jacoblewis.dailyexpense.fragments.enterPayment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.Toolbar
import android.view.*
import butterknife.BindView
import butterknife.ButterKnife
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.AnimationUtils
import me.jacoblewis.dailyexpense.commons.RevealAnimationSetting
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.NavigationController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment

class EnterPaymentFragment : Fragment(), RootFragment {
    override val screenTag: String = EnterPaymentFragment::class.java.name
    override val transitionIn: Int = R.anim.nothing
    override val transitionOut: Int = R.anim.nothing
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.edittext_enter_price)
    lateinit var enterPriceEditText: AppCompatEditText
    private lateinit var navigationController: NavigationController

    override fun setRootElevation(el: Float) {
        view?.elevation = el
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        MyApp.graph.inject(this)

        navigationController = context as NavigationController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_enter_payment, container, false)
        ButterKnife.bind(this, rootView)

        toolbar.title = "Enter Payment"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val revealSetting: RevealAnimationSetting = arguments?.get("reveal") as? RevealAnimationSetting
//                ?: return rootView
//
//        AnimationUtils.registerCircularRevealAnimation(context!!, rootView, revealSetting, ContextCompat.getColor(context!!, R.color.colorAccent), ContextCompat.getColor(context!!, R.color.white))
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.enter_payment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_continue -> {
                val price: Float = enterPriceEditText.text.toString().toFloat()
                navigationController.navigateTo(NavScreen.ChooseCategory(Payment(cost = price)))

            }
            android.R.id.home -> activity?.onBackPressed()
        }
        return true
    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        val revealSetting: RevealAnimationSetting = arguments?.get("reveal") as? RevealAnimationSetting
                ?: return false
        AnimationUtils.startCircularExitAnimation(context!!, view!!, revealSetting, ContextCompat.getColor(context!!, R.color.white), ContextCompat.getColor(context!!, R.color.colorAccent))
        return false
    }

    companion object {
        fun createWithRevealAnimation(revealAnimationSetting: RevealAnimationSetting): EnterPaymentFragment {
            val frag = EnterPaymentFragment()
            frag.arguments = Bundle().also { it.putParcelable("reveal", revealAnimationSetting) }
            return frag
        }
    }
}