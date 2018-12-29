package me.jacoblewis.dailyexpense.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.adapters.viewholders.BudgetOverviewViewHolder
import me.jacoblewis.dailyexpense.adapters.viewholders.CategoryChooseViewHolder
import me.jacoblewis.dailyexpense.adapters.viewholders.CategoryEditViewHolder
import me.jacoblewis.dailyexpense.commons.BudgetBalancer
import me.jacoblewis.dailyexpense.commons.CategoryBalancer
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerAdapter
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder
import javax.inject.Inject

// List Adapter
class CategoryItemAdapter
@Inject constructor(context: Context?, val db: BalancesDB, val sp: SharedPreferences) : RBRecyclerAdapter<Any, ItemDelegate<Any>>(context, null) {
    lateinit var recyclerView: RecyclerView
    var editable: Boolean = false

    val saveItemsDelegate: (pos: Int) -> Unit = { pos ->
        GlobalScope.launch {
            // Subtract 1 from the index and pos because the first item in the RecyclerView is the overall budget
            val pinned = itemList.mapIndexedNotNull { index, item -> if (item is Category && item.locked) index - 1 else null }.toMutableList().also { it.add(pos - 1) }
            val categories = itemList.mapNotNull { it as? Category }
            CategoryBalancer.balanceCategories(categories, pinned)
            db.categoriesDao().updateCategories(categories)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RBRecyclerViewHolder<*, *> {
        val budget = BudgetBalancer.budgetFromSharedPrefs(sp)
        return when {
            itemList[i] is Stats -> BudgetOverviewViewHolder(viewGroup)
            editable -> CategoryEditViewHolder(viewGroup)
            else -> CategoryChooseViewHolder(viewGroup, budget)
        }
    }

    override fun getItemViewType(position: Int) = position
}