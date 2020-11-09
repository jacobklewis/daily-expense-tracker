package me.jacoblewis.dailyexpense.fragments.categories

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import kotlinx.android.synthetic.main.content_appbar_recyclerview.view.*
import kotlinx.android.synthetic.main.fragment_main_content.view.*
import kotlinx.android.synthetic.main.fragment_main_content.view.toolbar
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.GeneralItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.CombinedLiveData
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.commons.StatsType
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.dailyexpense.viewModels.CategoryViewModel
import me.jacoblewis.jklcore.components.recyclerview.IdItem
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class CategoryOverviewFragment : RootFragment(R.layout.fragment_category_content), ItemDelegate<Any> {
    override val options: RootFragmentOptions = RootFragmentOptions(CategoryEditFragment::class.java, drawerNavId = R.id.menu_item_categories)

    val categoryAdapter: GeneralItemAdapter by inject()

    val viewModel: CategoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewBound(view: View) {
        view.toolbar.title = "Categories"
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryAdapter.callback = this
        view.recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        view.recycler_view.adapter = categoryAdapter

        navigationController.linkToolBarToDrawer(view.toolbar)

        viewModel.updateCategoryDate(DateHelper.firstDayOfMonth(Date(), TimeZone.getDefault()))

        CombinedLiveData(viewModel.categories, viewModel.remainingBudget) { data1, data2 ->
            data1 to data2
        }.observe(this, {
            val (cats, remainingBudget) = it
            val items: MutableList<IdItem<*>> = mutableListOf()
            if (remainingBudget != null && cats != null) {
                items.add(Stats(remainingBudget, cats, StatsType.PieChart))
                items.addAll(cats)
            }
            categoryAdapter.submitList(items)
        })

        view.fab_add_new.setOnClickListener {
            navigationController.navigateTo(NavScreen.EditCategories)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_category_overview, menu)
        val menuItem = menu.findItem(R.id.spinner)
        val monthSpinner = menuItem.actionView as AppCompatSpinner

        val data = viewModel.getPreviousMonths(3)
        val mapped = data.map { mapOf(Pair("title", it.display)) }

        val simpleAdapter = SimpleAdapter(context, mapped, R.layout.simple_dropdown_item_custom, arrayOf("title"), intArrayOf(android.R.id.text1))
        monthSpinner.adapter = simpleAdapter
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val cal = data[position].calendar
                viewModel.updateCategoryDate(cal, DateHelper.endOfMonth(cal))
            }

        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onItemClicked(item: Any) {

    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        return false
    }
}