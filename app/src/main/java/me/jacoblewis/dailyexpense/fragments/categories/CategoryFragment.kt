package me.jacoblewis.dailyexpense.fragments.categories

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.google.android.material.snackbar.Snackbar
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.CategoryController
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.CategoryBalancer
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.commons.SwipeCallback
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import javax.inject.Inject

class CategoryFragment : RootFragment(R.layout.fragment_category_content), ItemDelegate<Category> {
    override val options: RootFragmentOptions = RootFragmentOptions(CategoryFragment::class.java, drawerNavId = R.id.menu_item_categories)

    init {
        MyApp.graph.inject(this)
    }
    @Inject
    lateinit var categoryAdapter: CategoryController.CategoryItemAdapter

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.recycler_view_category)
    lateinit var recyclerView: RecyclerView

    private val viewModel: CategoryViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(CategoryViewModel::class.java)
    }

    override fun onViewBound(view: View) {
        toolbar.title = "Payment Category"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryAdapter.setCallback(this)
        categoryAdapter.editable = true
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.adapter = categoryAdapter

        val removeHandler = object : SwipeCallback(context!!, ContextCompat.getDrawable(context!!,R.drawable.ic_remove)!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Snackbar.make(view, "Remove it", Snackbar.LENGTH_SHORT).show()
                val pos = viewHolder.adapterPosition
                val item = categoryAdapter.itemList[pos]
                viewModel.removeCategory(item)
            }
        }
        val lockHandler = object : SwipeCallback(context!!, ContextCompat.getDrawable(context!!,R.drawable.ic_locked)!!, ItemTouchHelper.RIGHT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (CategoryBalancer.attemptLockToggle(categoryAdapter.itemList, viewHolder.adapterPosition)) {
                    Snackbar.make(view, "Toggle lock", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(view, "Must have at least two categories unlocked", Snackbar.LENGTH_LONG).show()
                }
                viewModel.updateCategories(categoryAdapter.itemList)
            }
        }
        val itemTouchHelper = ItemTouchHelper(removeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        val itemTouchHelper2 = ItemTouchHelper(lockHandler)
        itemTouchHelper2.attachToRecyclerView(recyclerView)

        navigationController.linkToolBarToDrawer(toolbar)
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

    override fun onItemClicked(item: Category) {

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