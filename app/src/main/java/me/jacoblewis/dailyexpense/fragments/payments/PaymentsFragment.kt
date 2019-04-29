package me.jacoblewis.dailyexpense.fragments.payments

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.google.android.material.snackbar.Snackbar
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.GeneralItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.commons.revealSettingsTo
import me.jacoblewis.dailyexpense.data.models.Footer
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.extensions.addSwipeListener
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.dailyexpense.viewModels.PaymentViewModel
import me.jacoblewis.jklcore.components.recyclerview.IdItem
import javax.inject.Inject

class PaymentsFragment : RootFragment(R.layout.fragment_payments_content), ItemDelegate<Any> {
    override val options: RootFragmentOptions = RootFragmentOptions(PaymentsFragment::class.java, drawerNavId = R.id.menu_item_payments)

    init {
        MyApp.graph.inject(this)
    }

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var paymentAdapter: GeneralItemAdapter

    private val viewModel: PaymentViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(PaymentViewModel::class.java)
    }

    private val removePaymentListener: (Int) -> Unit = { pos ->
        paymentAdapter.notifyItemChanged(pos)
        view?.let { view ->
            Snackbar.make(view, "Are you sure you want to delete?", Snackbar.LENGTH_LONG)
                    .setAction("Confirm Delete") {
                        val item = paymentAdapter.currentList[pos]
                        if (item is PaymentCategory && item.transaction != null) {
                            viewModel.removePayment(coroutineScope, item.transaction!!)
                        }
                    }.show()
        }
    }

    override fun onViewBound(view: View) {
        toolbar.title = "Payments"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        paymentAdapter.callback = this
        paymentAdapter.editable = true
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = paymentAdapter

        recyclerView.addSwipeListener(ItemTouchHelper.LEFT, R.drawable.ic_remove, staticTop = false, onSwipedListener = removePaymentListener)

        navigationController.linkToolBarToDrawer(toolbar)

        viewModel.payments.observe(this, Observer {
            if (it != null) {
                paymentAdapter.submitList(mutableListOf<IdItem<*>>().apply {
                    addAll(it.first)
                    add(Footer("${it.first.size} Payments"))
                })
            }
        })
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