package me.jacoblewis.dailyexpense.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
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
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder

class GeneralItemAdapter(context: Context?, val db: BalancesDB, balanceManager: BalanceManager) : ListAdapter<IdItem<*>, RBRecyclerViewHolder<IdItem<*>, ItemDelegate<Any>?>>(GeneralDiffUtil()) {
    var callback: ItemDelegate<Any>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RBRecyclerViewHolder<IdItem<*>, ItemDelegate<Any>?> {
        return when (viewType) {
            // Stats
            BudgetOverviewViewHolder.LAYOUT_TYPE -> BudgetOverviewViewHolder(parent)
            NextDayBudgetViewHolder.LAYOUT_TYPE -> NextDayBudgetViewHolder(parent)
            StatPieViewHolder.LAYOUT_TYPE -> StatPieViewHolder(parent)
            StatPressureViewHolder.LAYOUT_TYPE -> StatPressureViewHolder(parent)
            // Categories
            CategoryEditViewHolder.LAYOUT_TYPE -> CategoryEditViewHolder(parent)
            CategoryChooseViewHolder.LAYOUT_TYPE -> CategoryChooseViewHolder(parent, budget)
            // Payments
            PaymentViewHolder.LAYOUT_TYPE -> PaymentViewHolder(parent)
            // Footer
            FooterViewHolder.LAYOUT_TYPE -> FooterViewHolder(parent)
            else -> throw Exception("No ViewHolder for item")
        } as RBRecyclerViewHolder<IdItem<*>, ItemDelegate<Any>?>
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            // Stats
            is Stats -> when (item.displayType) {
                is StatsType.Overview -> BudgetOverviewViewHolder.LAYOUT_TYPE
                is StatsType.NextDay -> NextDayBudgetViewHolder.LAYOUT_TYPE
                is StatsType.PieChart -> StatPieViewHolder.LAYOUT_TYPE
                is StatsType.PressureMeter -> StatPressureViewHolder.LAYOUT_TYPE
            }
            // Categories
            is Category -> when {
                editable -> CategoryEditViewHolder.LAYOUT_TYPE
                else -> CategoryChooseViewHolder.LAYOUT_TYPE
            }
            // Payments
            is PaymentCategory -> PaymentViewHolder.LAYOUT_TYPE
            // Footer
            is Footer -> FooterViewHolder.LAYOUT_TYPE
            else -> throw Exception("No ViewHolder for item")
        }
    }

    override fun onBindViewHolder(holder: RBRecyclerViewHolder<IdItem<*>, ItemDelegate<Any>?>, position: Int) {
        holder.bindItem(getItem(position), position, callback)
    }


    lateinit var recyclerView: RecyclerView
    var editable: Boolean = false
    private val budget = balanceManager.currentBudget
}