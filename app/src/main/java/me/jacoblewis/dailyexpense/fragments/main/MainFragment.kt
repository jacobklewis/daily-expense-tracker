package me.jacoblewis.dailyexpense.fragments.main

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.OnClick
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.adapters.PaymentsController
import me.jacoblewis.dailyexpense.commons.*
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.jklcore.components.recyclerview.IdItem

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

    var appBarLayoutState: State = State.EXPANDED

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private val paymentAdapter: PaymentsController.PaymentItemAdapter by lazy { PaymentsController.createAdapter(context!!, this) as PaymentsController.PaymentItemAdapter }

    override fun onViewBound(view: View) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        appBarLayout.addStateChangeListener { _, state ->
            appBarLayoutState = state
            updateTitle()
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
                val items: MutableList<IdItem<*>> = mutableListOf()
                items.add(Stats(it.second))
                items.addAll(it.first)
                paymentAdapter.updateItems(items)
            }
        })

        viewModel.dailyBalance.observe(this, Observer {
            updateTitle()
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkCurrentDay()
    }

    private fun updateTitle() {
        val dailyBalance = viewModel.dailyBalance.value?.asCurrency
        if (dailyBalance != null) {
            collapsingToolbarLayout.title = when (appBarLayoutState) {
                State.COLLAPSED -> "$dailyBalance - Daily Balance"
                else -> dailyBalance
            }
        } else {
            collapsingToolbarLayout.title = ""
        }
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