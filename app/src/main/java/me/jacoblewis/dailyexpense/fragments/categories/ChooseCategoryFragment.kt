package me.jacoblewis.dailyexpense.fragments.categories

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.content_appbar_recyclerview.view.*
import kotlinx.android.synthetic.main.fragment_enter_payment.view.toolbar
import kotlinx.android.synthetic.main.fragment_main_content.view.*
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.GeneralItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.ARG_PAYMENT
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.dailyexpense.viewModels.CategoryViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class ChooseCategoryFragment : RootFragment(R.layout.fragment_category_content), ItemDelegate<Any> {
    override val options: RootFragmentOptions = RootFragmentOptions(ChooseCategoryFragment::class.java)

    val categoryAdapter: GeneralItemAdapter by inject()

    val viewModel: CategoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.associatedPayment = arguments?.get(ARG_PAYMENT) as? Payment
    }

    override fun onViewBound(view: View) {
        val currentPayment = (viewModel.associatedPayment?.cost ?: 0f).asCurrency
        view.toolbar.title = "$currentPayment - Payment Category"
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryAdapter.callback = this
        view.recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        view.recycler_view.adapter = categoryAdapter

        viewModel.categories.observe(this, Observer {
            if (it != null) {
                categoryAdapter.submitList(it)
            }
        })
        viewModel.updateCategoryDate(DateHelper.firstDayOfMonth(Date(), TimeZone.getDefault()))

        view.fab_add_new.setOnClickListener {
            navigationController.navigateTo(NavScreen.EditCategories)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.onBackPressed()
        }
        return true
    }

    override fun onItemClicked(item: Any) {
        if (item is Category) {
            viewModel.applyCategoryToPayment(item)
            viewModel.commitPayment(coroutineScope)

            navigationController.navigateTo(NavScreen.Payments)
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