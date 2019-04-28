package me.jacoblewis.dailyexpense.fragments.categories

import android.graphics.drawable.Icon
import android.os.Bundle
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.GeneralItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.extensions.addSwipeListener
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.dailyexpense.viewModels.CategoryViewModel
import javax.inject.Inject

class CategoryEditFragment : RootFragment(R.layout.fragment_category_content), ItemDelegate<Any> {
    override val options: RootFragmentOptions = RootFragmentOptions(CategoryEditFragment::class.java, drawerNavId = R.id.menu_item_categories)

    init {
        MyApp.graph.inject(this)
    }

    @Inject
    lateinit var categoryAdapter: GeneralItemAdapter

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.fab_add_new)
    lateinit var fab: FloatingActionButton

    private val viewModel: CategoryViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(CategoryViewModel::class.java)
    }

    private val removeCategoryListener: (Int) -> Unit = { pos ->
        categoryAdapter.notifyItemChanged(pos)
        view?.let { view ->
            Snackbar.make(view, "Are you sure you want to delete?", Snackbar.LENGTH_LONG)
                    .setAction("Confirm Delete") {
                        val item = categoryAdapter.currentList[pos]
                        if (item is Category) {
                            viewModel.removeCategory(coroutineScope, item)
                        }
                    }.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewBound(view: View) {
        toolbar.title = "Edit Category Budgets"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fab.setImageIcon(Icon.createWithResource(context, R.drawable.ic_baseline_add_24px))

        categoryAdapter.callback = this
        categoryAdapter.editable = true
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.adapter = categoryAdapter

        recyclerView.addSwipeListener(ItemTouchHelper.LEFT, R.drawable.ic_remove, staticTop = false, onSwipedListener = removeCategoryListener)

        viewModel.updateCategoryDate(DateHelper.beginningOfTime)
    }

    override fun onStart() {
        super.onStart()
        viewModel.categories.observe(this, Observer {
            if (it != null) {
                categoryAdapter.submitList(it)
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
        navigationController.navigateTo(NavScreen.EditCategory(item as Category))
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