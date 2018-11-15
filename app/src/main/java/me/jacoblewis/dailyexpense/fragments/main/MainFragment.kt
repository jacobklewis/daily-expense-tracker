package me.jacoblewis.dailyexpense.fragments.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.*
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.adapters.PaymentsController
import me.jacoblewis.dailyexpense.commons.State
import me.jacoblewis.dailyexpense.commons.addStateChangeListener
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import javax.inject.Inject

class MainFragment : Fragment(), RootFragment, ItemDelegate<PaymentCategory> {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.drawer_layout)
    lateinit var drawerLayout: DrawerLayout
    @BindView(R.id.navigation_view)
    lateinit var navView: NavigationView
    @BindView(R.id.main_appbar)
    lateinit var appBarLayout: AppBarLayout
    @BindView(R.id.main_collapsing)
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    @BindView(R.id.main_recycler_view)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.fab_add_new)
    lateinit var addNewFab: FloatingActionButton

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private val paymentAdapter: PaymentsController.PaymentItemAdapter by lazy { PaymentsController.createAdapter(context, this) as PaymentsController.PaymentItemAdapter }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        MyApp.graph.inject(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        ButterKnife.bind(this, rootView)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.title_open_nav, R.string.title_close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setCheckedItem(R.id.menu_item_overview)
        navView.setNavigationItemSelectedListener {
            return@setNavigationItemSelectedListener true
        }
        val money = "$203.50"

        appBarLayout.addStateChangeListener { _, state ->
            collapsingToolbarLayout.title = when (state) {
                State.COLLAPSED -> "$money - Daily Balance"
                else -> money
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = paymentAdapter

        viewModel.payments.observe(this, Observer {
            if (it != null) {
                paymentAdapter.removeAllItems()
                paymentAdapter.addItems(it)
            }
        })

        return rootView
    }

    override fun onItemClicked(item: PaymentCategory) {
        Snackbar.make(view!!, "${item.transaction?.cost ?: "N/A"}", Snackbar.LENGTH_SHORT).show()
    }

    @OnClick(R.id.fab_add_new)
    fun addNewFabClicked(v: View) {
        viewModel.addMockPayment()
    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
        return false
    }
}