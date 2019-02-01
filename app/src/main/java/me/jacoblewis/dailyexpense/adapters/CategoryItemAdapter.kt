package me.jacoblewis.dailyexpense.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.jacoblewis.dailyexpense.adapters.viewholders.BudgetOverviewViewHolder
import me.jacoblewis.dailyexpense.adapters.viewholders.CategoryChooseViewHolder
import me.jacoblewis.dailyexpense.adapters.viewholders.CategoryEditViewHolder
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.dailyexpense.managers.BalanceManager
import me.jacoblewis.jklcore.components.recyclerview.IdItem
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerAdapter
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder
import javax.inject.Inject

// TODO: switch to List Adapter
class CategoryItemAdapter
@Inject constructor(context: Context?, val db: BalancesDB, balanceManager: BalanceManager) : RBRecyclerAdapter<IdItem<*>, ItemDelegate<Any>>(context, null) {
    lateinit var recyclerView: RecyclerView
    var editable: Boolean = false
    private val budget = balanceManager.currentBudget

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RBRecyclerViewHolder<IdItem<*>, ItemDelegate<Any>?> {

        return when {
            itemList[i] is Stats -> BudgetOverviewViewHolder(viewGroup)
            editable -> CategoryEditViewHolder(viewGroup)
            else -> CategoryChooseViewHolder(viewGroup, budget)
        } as RBRecyclerViewHolder<IdItem<*>, ItemDelegate<Any>?>
    }

    override fun getItemViewType(position: Int) = position
}