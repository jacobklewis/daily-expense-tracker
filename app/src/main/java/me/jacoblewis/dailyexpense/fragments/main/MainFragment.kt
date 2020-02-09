package me.jacoblewis.dailyexpense.fragments.main

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.BindView
import butterknife.OnClick
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.GeneralItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.*
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.dailyexpense.viewModels.CategoryViewModel
import me.jacoblewis.dailyexpense.viewModels.PaymentViewModel
import me.jacoblewis.jklcore.components.recyclerview.IdItem
import java.util.*
import javax.inject.Inject

class MainFragment : RootFragment(R.layout.fragment_main_content), ItemDelegate<Any> {
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

    @Inject
    lateinit var overviewAdapter: GeneralItemAdapter

    var appBarLayoutState: State = State.EXPANDED

    private val paymentViewModel: PaymentViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(PaymentViewModel::class.java)
    }
    private val categoryViewModel: CategoryViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(CategoryViewModel::class.java)
    }

    override fun onViewBound(view: View) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        appBarLayout.addStateChangeListener { _, state ->
            appBarLayoutState = state
            updateTitle()
        }

        overviewAdapter.callback = this
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = overviewAdapter

        navigationController.linkToolBarToDrawer(toolbar)

        categoryViewModel.updateCategoryDate(DateHelper.firstDayOfMonth(Date(), TimeZone.getDefault()))

        observeBoth(paymentViewModel.payments, categoryViewModel.categories, this) { payments, cats ->
            val items: MutableList<IdItem<*>> = mutableListOf()
            // Add our items here
            items.add(Stats(payments.second))
            items.add(Stats(payments.second, cats, StatsType.PressureMeter))
            items.add(Stats(payments.second, cats, StatsType.PieChart))
            overviewAdapter.submitList(items)
        }

        paymentViewModel.dailyBalance.observe(this, Observer {
            updateTitle()
        })
    }

    override fun onStart() {
        super.onStart()
        collapsingToolbarLayout.post { collapsingToolbarLayout.requestLayout() }
    }

    override fun onResume() {
        super.onResume()
        paymentViewModel.checkCurrentDay()
    }

    private fun updateTitle() {
        val dailyBalance = paymentViewModel.dailyBalance.value?.asCurrency
        if (dailyBalance != null) {
            collapsingToolbarLayout.title = when (appBarLayoutState) {
                State.COLLAPSED -> "$dailyBalance - Daily Balance"
                else -> dailyBalance
            }
        } else {
            collapsingToolbarLayout.title = ""
        }
    }

    override fun onItemClicked(item: Any) {
        if (item is PaymentCategory) {
            Snackbar.make(view!!, "${item.transaction?.cost
                    ?: "N/A"}", Snackbar.LENGTH_SHORT).show()
        }
    }

    @OnClick(R.id.fab_add_new)
    fun addNewFabClicked(v: View) {
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