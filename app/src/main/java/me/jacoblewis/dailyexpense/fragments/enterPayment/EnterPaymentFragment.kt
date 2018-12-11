package me.jacoblewis.dailyexpense.fragments.enterPayment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import butterknife.BindView
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.AnimationUtils
import me.jacoblewis.dailyexpense.commons.RevealAnimationSetting
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment

class EnterPaymentFragment : RootFragment(R.layout.fragment_enter_payment) {
    override val options: RootFragmentOptions = RootFragmentOptions(EnterPaymentFragment::class.java)

    init {
        MyApp.graph.inject(this)
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.edittext_enter_price)
    lateinit var enterPriceEditText: AppCompatEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewBound(view: View) {
        toolbar.title = "Enter Payment"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val revealSetting: RevealAnimationSetting = arguments?.get("reveal") as? RevealAnimationSetting
//                ?: return rootView
//
//        AnimationUtils.registerCircularRevealAnimation(context!!, rootView, revealSetting, ContextCompat.getColor(context!!, R.color.colorAccent), ContextCompat.getColor(context!!, R.color.white))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.enter_payment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_continue -> {
                val price: Float = enterPriceEditText.text.toString().replace("$", "").toFloat()
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