package me.jacoblewis.dailyexpense.fragments.main

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_appbar_recyclerview.view.main_appbar
import kotlinx.android.synthetic.main.content_appbar_recyclerview.view.toolbar
import kotlinx.android.synthetic.main.fragment_main_content.view.*
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.GeneralItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.*
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.dailyexpense.viewModels.CategoryViewModel
import me.jacoblewis.dailyexpense.viewModels.PaymentViewModel
import me.jacoblewis.jklcore.components.recyclerview.IdItem
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class MainFragment : RootFragment(R.layout.fragment_main_content), ItemDelegate<Any> {
    override val options: RootFragmentOptions = RootFragmentOptions(MainFragment::class.java, drawerNavId = R.id.menu_item_overview)


    val overviewAdapter: GeneralItemAdapter by inject()

    var appBarLayoutState: State = State.EXPANDED

    private val paymentViewModel: PaymentViewModel by viewModel()
    private val categoryViewModel: CategoryViewModel by viewModel()

    override fun onViewBound(view: View) {
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar)

        view.main_appbar.addStateChangeListener { _, state ->
            appBarLayoutState = state
            updateTitle()
        }

        overviewAdapter.callback = this
        view.recycler_view_main.layoutManager = LinearLayoutManager(context)
        view.recycler_view_main.adapter = overviewAdapter

        navigationController.linkToolBarToDrawer(view.toolbar)

        categoryViewModel.updateCategoryDate(DateHelper.firstDayOfMonth(Date(), TimeZone.getDefault()))



        MainFragmentStateModel.mergeSources(paymentViewModel, categoryViewModel).observe(this, { model ->
            val items: MutableList<IdItem<*>> = mutableListOf()
            // Add our items here
            if (model.dailyPressure != null && model.nextDayBalance != null && model.remainingBudget != null && model.categories != null) {
                items.add(Stats(0f, displayType = StatsType.PressureMeter, pressure = model.dailyPressure))
                model.nextDayBalance.forEach { nextDay ->
                    items.add(Stats(nextDay.budget, displayType = StatsType.NextDay, date = nextDay.date))
                }
                items.add(Stats(model.remainingBudget))
                items.add(Stats(0f, model.categories, StatsType.PieChart))
            }

            overviewAdapter.submitList(items)
        })

        paymentViewModel.dailyBalance.observe(this, Observer {
            updateTitle()
        })
        view.fab_add_new.setOnClickListener {
            navigationController.navigateTo(NavScreen.EnterPayment(it revealSettingsTo view))
        }
    }

    override fun onStart() {
        super.onStart()
        view?.main_collapsing?.post { view?.main_collapsing?.requestLayout() }
    }

    override fun onResume() {
        super.onResume()
        paymentViewModel.checkCurrentDay()
    }

    private fun updateTitle() {
        val dailyBalance = paymentViewModel.dailyBalance.value?.asCurrency
        if (dailyBalance != null) {
            view?.main_collapsing?.title = when (appBarLayoutState) {
                State.COLLAPSED -> "$dailyBalance - Daily Balance"
                else -> dailyBalance
            }
        } else {
            view?.main_collapsing?.title = ""
        }
    }

    override fun onItemClicked(item: Any) {
        if (item is PaymentCategory) {
            Snackbar.make(requireView(), "${
                item.transaction?.cost
                        ?: "N/A"
            }", Snackbar.LENGTH_SHORT).show()
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