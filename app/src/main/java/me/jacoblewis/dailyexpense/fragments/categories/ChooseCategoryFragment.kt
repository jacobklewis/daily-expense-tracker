package me.jacoblewis.dailyexpense.fragments.categories

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.OnClick
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.GeneralItemAdapter
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.*
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import me.jacoblewis.dailyexpense.viewModels.CategoryViewModel
import java.util.*
import javax.inject.Inject

class ChooseCategoryFragment : RootFragment(R.layout.fragment_category_content), ItemDelegate<Any> {
    override val options: RootFragmentOptions = RootFragmentOptions(ChooseCategoryFragment::class.java)

    init {
        MyApp.graph.inject(this)
    }

    @Inject
    lateinit var categoryAdapter: GeneralItemAdapter

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView

    private val viewModel: CategoryViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(CategoryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.associatedPayment = arguments?.get(ARG_PAYMENT) as? Payment
    }

    override fun onViewBound(view: View) {
        val currentPayment = (viewModel.associatedPayment?.cost ?: 0f).asCurrency
        toolbar.title = "$currentPayment - Payment Category"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryAdapter.setCallback(this)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.adapter = categoryAdapter

        viewModel.categories.observe(this, Observer {
            if (it != null) {
                categoryAdapter.updateItems(it)
            }
        })
        viewModel.updateCategoryDate(DateHelper.firstDayOfMonth(Date(), TimeZone.getDefault()))
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

    @OnClick(R.id.fab_add_new)
    fun addNewCategory(v: View) {
        navigationController.navigateTo(NavScreen.EditCategories)
    }

    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    override fun navigateBack(): Boolean {
        val revealSetting: RevealAnimationSetting = arguments?.get("reveal") as? RevealAnimationSetting
                ?: return false
        AnimationUtils.startCircularExitAnimation(context!!, view!!, revealSetting, ContextCompat.getColor(context!!, R.color.white), ContextCompat.getColor(context!!, R.color.colorAccent))
        return false
    }
}