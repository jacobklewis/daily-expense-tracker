package me.jacoblewis.dailyexpense.fragments.categories

import android.view.MenuItem
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
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.extensions.addSwipeListener
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import javax.inject.Inject

class CategoryEditFragment : RootFragment(R.layout.fragment_category_content), ItemDelegate<Any> {
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
        toolbar.title = "Edit Category Budgets"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryAdapter.setCallback(this)
        categoryAdapter.editable = true
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.adapter = categoryAdapter

        recyclerView.addSwipeListener(ItemTouchHelper.LEFT, R.drawable.ic_remove, staticTop = false) { pos ->
            Snackbar.make(view, "Remove it", Snackbar.LENGTH_SHORT).show()
            val item = categoryAdapter.itemList[pos]
            if (item is Category) {
                viewModel.removeCategory(item)
            }
        }

        viewModel.updateCategoryDate(DateHelper.beginningOfTime)
    }

    override fun onStart() {
        super.onStart()
        viewModel.categories.observe(this, Observer {
            if (it != null) {
                categoryAdapter.updateItems(it)
                categoryAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.onBackPressed()
        }
        return true
    }

    override fun onItemClicked(item: Any) {
        Snackbar.make(view!!, "TODO: Edit this!", Snackbar.LENGTH_LONG).show()
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