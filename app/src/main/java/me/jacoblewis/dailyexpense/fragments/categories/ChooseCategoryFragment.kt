package me.jacoblewis.dailyexpense.fragments.categories

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.CategoryController
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.ARG_PAYMENT
import me.jacoblewis.dailyexpense.commons.AnimationUtils
import me.jacoblewis.dailyexpense.commons.RevealAnimationSetting
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.interfaces.NavigationController
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavScreen
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.RootFragment
import javax.inject.Inject

class ChooseCategoryFragment : Fragment(), RootFragment, ItemDelegate<Category> {
    override val screenTag: String = ChooseCategoryFragment::class.java.name
    override val transitionIn: Int = R.anim.nothing
    override val transitionOut: Int = R.anim.nothing

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.recycler_view_category)
    lateinit var recyclerView: RecyclerView
    private lateinit var navigationController: NavigationController


    private val viewModel: CategoryViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(CategoryViewModel::class.java)
    }

    private val categoryAdapter: CategoryController.CategoryItemAdapter by lazy { CategoryController.createAdapter(context, this) as CategoryController.CategoryItemAdapter }

    override fun setRootElevation(el: Float) {
        view?.elevation = el
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        MyApp.graph.inject(this)

        navigationController = context as NavigationController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_category_content, container, false)
        ButterKnife.bind(this, rootView)

        toolbar.title = "Payment Category"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = categoryAdapter

        viewModel.categories.observe(this, Observer {
            if (it != null) {
                categoryAdapter.removeAllItems()
                categoryAdapter.addItems(it)
            }
        })

        return rootView
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> activity?.onBackPressed()
        }
        return true
    }

    override fun onItemClicked(item: Category) {
        val payment: Payment = arguments?.get(ARG_PAYMENT) as? Payment ?: Payment(0f)
        payment.categoryId = item.categoryId
        viewModel.savePayment(payment)

        navigationController.navigateTo(NavScreen.Main, navBack = true)
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
        val revealSetting: RevealAnimationSetting = arguments?.get("reveal") as? RevealAnimationSetting
                ?: return false
        AnimationUtils.startCircularExitAnimation(context!!, view!!, revealSetting, ContextCompat.getColor(context!!, R.color.white), ContextCompat.getColor(context!!, R.color.colorAccent))
        return false
    }
}