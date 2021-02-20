package me.jacoblewis.dailyexpense.fragments.categories

import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_appbar_recyclerview.view.*
import kotlinx.android.synthetic.main.fragment_main_content.view.*
import kotlinx.android.synthetic.main.fragment_main_content.view.toolbar
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.GeneralItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.commons.RootFragmentOptions
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.extensions.addSwipeListener
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.dailyexpense.viewModels.CategoryViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class CategoryEditFragment : RootFragment(R.layout.fragment_category_content), ItemDelegate<Any> {
    override val options: RootFragmentOptions = RootFragmentOptions(CategoryEditFragment::class.java, drawerNavId = R.id.menu_item_categories)

    val categoryAdapter: GeneralItemAdapter by inject()

    val viewModel: CategoryViewModel by viewModel()

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
        view.toolbar.title = "Edit Category Budgets"
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        view.fab_add_new.setImageIcon(Icon.createWithResource(context, R.drawable.ic_baseline_add_24px))

        categoryAdapter.callback = this
        categoryAdapter.editable = true
        view.recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        view.recycler_view.adapter = categoryAdapter

        view.recycler_view.addSwipeListener(ItemTouchHelper.LEFT, R.drawable.ic_remove, staticTop = false, onSwipedListener = removeCategoryListener)

        viewModel.updateCategoryDate(DateHelper.beginningOfTime)
        view.fab_add_new.setOnClickListener {
            navigationController.navigateTo(NavScreen.EnterCategory)
        }
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


    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        return false
    }
}