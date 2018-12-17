package me.jacoblewis.dailyexpense.fragments.categories

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.google.android.material.snackbar.Snackbar
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.CategoryItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.CategoryBalancer
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.extensions.addSwipeListener
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import javax.inject.Inject

class CategoryFragment : RootFragment(R.layout.fragment_category_content), ItemDelegate<Any> {
    override val options: RootFragmentOptions = RootFragmentOptions(CategoryFragment::class.java, drawerNavId = R.id.menu_item_categories)

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
        toolbar.title = "Configure Category Budgets"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryAdapter.setCallback(this)
        categoryAdapter.editable = true
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.adapter = categoryAdapter

        recyclerView.addSwipeListener(ItemTouchHelper.LEFT, R.drawable.ic_remove, true) { pos ->
            Snackbar.make(view, "Remove it", Snackbar.LENGTH_SHORT).show()
            val item = categoryAdapter.itemList[pos]
            if (item is Category) {
                viewModel.removeCategory(item)
            }
        }
        recyclerView.addSwipeListener(ItemTouchHelper.RIGHT, R.drawable.ic_locked, true) { pos ->
            val cats = viewModel.categories.value ?: return@addSwipeListener
            // Subtract 1 from pos because the first item is the budget overview
            if (CategoryBalancer.attemptLockToggle(cats, pos - 1)) {
                Snackbar.make(view, "Toggle lock", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(view, "Must have at least two categories unlocked", Snackbar.LENGTH_LONG).show()
            }
            viewModel.updateCategories(cats)
        }

        navigationController.linkToolBarToDrawer(toolbar)
    }

    override fun onStart() {
        super.onStart()
        viewModel.categories.observe(this, Observer {
            if (it != null) {
                val items: MutableList<Any> = mutableListOf()
                items.add(Stats(viewModel.budget))
                items.addAll(it)
                categoryAdapter.updateItems(items)
                categoryAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onItemClicked(item: Any) {

    }


    @OnClick(R.id.fab_add_new)
    fun addNewCategory(v: View) {
        navigationController.navigateTo(NavScreen.EnterCategory)
    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        return false
    }
}