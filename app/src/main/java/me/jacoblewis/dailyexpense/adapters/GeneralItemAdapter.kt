package me.jacoblewis.dailyexpense.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.jacoblewis.dailyexpense.adapters.viewholders.*
import me.jacoblewis.dailyexpense.commons.StatsType
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Footer
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.dailyexpense.managers.BalanceManager
import me.jacoblewis.jklcore.components.recyclerview.IdItem
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerAdapter
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder
import javax.inject.Inject

class GeneralItemAdapter
@Inject constructor(context: Context?, val db: BalancesDB, balanceManager: BalanceManager) : RBRecyclerAdapter<IdItem<*>, ItemDelegate<Any>>(context, null) {
    lateinit var recyclerView: RecyclerView
    var editable: Boolean = false
    private val budget = balanceManager.currentBudget

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): RBRecyclerViewHolder<IdItem<*>, ItemDelegate<Any>?> {
        val item = itemList[type]
        return when (item) {
            // Stats
            is Stats -> when (item.displayType) {
                is StatsType.Overview -> BudgetOverviewViewHolder(viewGroup)
                is StatsType.PieChart -> StatPieViewHolder(viewGroup)
            }
            // Categories
            is Category -> when {
                editable -> CategoryEditViewHolder(viewGroup)
                else -> CategoryChooseViewHolder(viewGroup, budget)
            }
            // Payments
            is PaymentCategory -> PaymentViewHolder(viewGroup)
            // Footer
            is Footer -> FooterViewHolder(viewGroup)
            else -> throw Exception("No ViewHolder for item")
        } as RBRecyclerViewHolder<IdItem<*>, ItemDelegate<Any>?>
    }

    override fun getItemViewType(position: Int) = position
}