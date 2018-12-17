package me.jacoblewis.dailyexpense.fragments.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.View
import butterknife.BindView
import butterknife.OnClick
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.adapters.PaymentsController
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.commons.State
import me.jacoblewis.dailyexpense.commons.addStateChangeListener
import me.jacoblewis.dailyexpense.commons.revealSettingsTo
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment

class MainFragment : RootFragment(R.layout.fragment_main_content), ItemDelegate<PaymentCategory> {
    override val options: RootFragmentOptions = RootFragmentOptions(MainFragment::class.java, drawerNavId = R.id.menu_item_overview)

    init {
        MyApp.graph.inject(this)
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.main_appbar)
    lateinit var appBarLayout: AppBarLayout
    @BindView(R.id.main_collapsing)
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    @BindView(R.id.recycler_view_main)
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private val paymentAdapter: PaymentsController.PaymentItemAdapter by lazy { PaymentsController.createAdapter(context, this) as PaymentsController.PaymentItemAdapter }

    override fun onViewBound(view: View) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)


        val money = "$203.50"

        appBarLayout.addStateChangeListener { _, state ->
            collapsingToolbarLayout.title = when (state) {
                State.COLLAPSED -> "$money - Daily Balance"
                else -> money
            }
        }

        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.adapter = paymentAdapter

        navigationController.linkToolBarToDrawer(toolbar)
    }

    override fun onStart() {
        super.onStart()
        collapsingToolbarLayout.post { collapsingToolbarLayout.requestLayout() }

        viewModel.payments.observe(this, Observer {
            if (it != null) {
                paymentAdapter.removeAllItems()
                paymentAdapter.addItems(it)
            }
        })
    }

    override fun onItemClicked(item: PaymentCategory) {
        Snackbar.make(view!!, "${item.transaction?.cost ?: "N/A"}", Snackbar.LENGTH_SHORT).show()
    }

    @OnClick(R.id.fab_add_new)
    fun addNewFabClicked(v: View) {
//        viewModel.addMockPayment()
        val v2 = view ?: return
        navigationController.navigateTo(NavScreen.EnterPayment(v revealSettingsTo v2))
    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        return false
    }
}