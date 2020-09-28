package me.jacoblewis.dailyexpense.fragments.payments

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_appbar_recyclerview.view.*
import kotlinx.android.synthetic.main.content_appbar_recyclerview.view.toolbar
import kotlinx.android.synthetic.main.fragment_main_content.view.*
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.GeneralItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.commons.revealSettingsTo
import me.jacoblewis.dailyexpense.data.models.Footer
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.extensions.addSwipeListener
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.dailyexpense.viewModels.PaymentViewModel
import me.jacoblewis.jklcore.components.recyclerview.IdItem
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class PaymentsFragment : RootFragment(R.layout.fragment_payments_content), ItemDelegate<Any> {
    override val options: RootFragmentOptions = RootFragmentOptions(PaymentsFragment::class.java, drawerNavId = R.id.menu_item_payments)

    val paymentAdapter: GeneralItemAdapter by inject()

    private val viewModel: PaymentViewModel by viewModel()

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
        view.toolbar.title = "Payments"
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar)

        paymentAdapter.callback = this
        paymentAdapter.editable = true
        view.recycler_view.layoutManager = LinearLayoutManager(context)
        view.recycler_view.adapter = paymentAdapter

        view.recycler_view.addSwipeListener(ItemTouchHelper.LEFT, R.drawable.ic_remove, staticTop = false, onSwipedListener = removePaymentListener)

        navigationController.linkToolBarToDrawer(view.toolbar)

        viewModel.payments.observe(this, Observer {
            if (it != null) {
                paymentAdapter.submitList(mutableListOf<IdItem<*>>().apply {
                    addAll(it.first)
                    add(Footer("${it.first.size} Payments"))
                })
            }
        })
        view.fab_add_new.setOnClickListener {
            navigationController.navigateTo(NavScreen.EnterPayment(it revealSettingsTo view))
        }
    }


    override fun onItemClicked(item: Any) {
        val v = view
        if (item is PaymentCategory && v != null) {
            Snackbar.make(v, "${item.transaction?.cost
                    ?: "N/A"}", Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        return false
    }
}