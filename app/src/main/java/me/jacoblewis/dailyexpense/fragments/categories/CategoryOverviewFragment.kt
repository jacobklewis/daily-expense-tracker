package me.jacoblewis.dailyexpense.fragments.categories

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.CategoryItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.commons.observeBoth
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.jklcore.components.recyclerview.IdItem
import java.util.*
import javax.inject.Inject

class CategoryOverviewFragment : RootFragment(R.layout.fragment_category_content), ItemDelegate<Any> {
    override val options: RootFragmentOptions = RootFragmentOptions(CategoryEditFragment::class.java, drawerNavId = R.id.menu_item_categories)

    init {
        MyApp.graph.inject(this)
    }

    @Inject
    lateinit var categoryAdapter: CategoryItemAdapter

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.recycler_view_category)
    lateinit var recyclerView: RecyclerView

    private val viewModel: CategoryViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(CategoryViewModel::class.java)
    }

    override fun onViewBound(view: View) {
        toolbar.title = "Categories"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryAdapter.setCallback(this)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.adapter = categoryAdapter

        navigationController.linkToolBarToDrawer(toolbar)

        viewModel.updateCategoryDate(DateHelper.firstDayOfMonth(Date(), TimeZone.getDefault()))
    }

    override fun onStart() {
        super.onStart()
        observeBoth(viewModel.categories, viewModel.remainingBudget, this) { cats, remainingBudget ->
            val items: MutableList<IdItem<*>> = mutableListOf()
            items.add(Stats(remainingBudget))
            items.addAll(cats)
            categoryAdapter.updateItems(items)
        }
    }

    override fun onItemClicked(item: Any) {

    }


    @OnClick(R.id.fab_add_new)
    fun addNewCategory(v: View) {
        navigationController.navigateTo(NavScreen.EditCategories)
    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        return false
    }
}