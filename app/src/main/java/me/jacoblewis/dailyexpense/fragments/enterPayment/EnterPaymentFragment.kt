package me.jacoblewis.dailyexpense.fragments.enterPayment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.OnClick
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.AnimationUtils
import me.jacoblewis.dailyexpense.commons.RevealAnimationSetting
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.dailyexpense.viewModels.EnterPaymentViewModel

class EnterPaymentFragment : RootFragment(R.layout.fragment_enter_payment) {
    override val options: RootFragmentOptions = RootFragmentOptions(EnterPaymentFragment::class.java)

    val numArr = intArrayOf(
            R.id.btn_num0,
            R.id.btn_num1,
            R.id.btn_num2,
            R.id.btn_num3,
            R.id.btn_num4,
            R.id.btn_num5,
            R.id.btn_num6,
            R.id.btn_num7,
            R.id.btn_num8,
            R.id.btn_num9
    )

    init {
        MyApp.graph.inject(this)
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.text_final_input)
    lateinit var finalInputText: TextView

    private val viewModel: EnterPaymentViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(EnterPaymentViewModel::class.java)
    }

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
        viewModel.finalInput.observe(this, Observer { payment ->
            finalInputText.text = payment.asCurrency
            // Only have continue button enabled if there is a payment
            view.findViewById<View>(R.id.btn_next).isEnabled = payment != 0f
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_enter_payment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_continue -> {
                navigateForwards()
            }
            android.R.id.home -> activity?.onBackPressed()
        }
        return true
    }

    @OnClick(R.id.btn_num0, R.id.btn_num1, R.id.btn_num2, R.id.btn_num3, R.id.btn_num4, R.id.btn_num5,
            R.id.btn_num6, R.id.btn_num7, R.id.btn_num8, R.id.btn_num9, R.id.btn_del, R.id.btn_next)
    fun onNumberPadClicked(v: View) {
        when (v.id) {
            R.id.btn_next -> navigateForwards()
            R.id.btn_del -> viewModel.removeDigit()
            else -> viewModel.addDigit(numArr.indexOf(v.id))
        }
    }

    private fun navigateForwards() {
        navigationController.navigateTo(NavScreen.ChooseCategory(Payment(cost = viewModel.finalInput.value
                ?: 0f)))
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