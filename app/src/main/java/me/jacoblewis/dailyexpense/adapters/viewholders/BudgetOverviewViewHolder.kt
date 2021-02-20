package me.jacoblewis.dailyexpense.adapters.viewholders

import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.viewholder_budget_overview.view.*
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder

class BudgetOverviewViewHolder(viewGroup: ViewGroup) : RBRecyclerViewHolder<Stats, ItemDelegate<Any>>(viewGroup, LAYOUT_TYPE) {

    override fun setUpView(itemView: View, item: Stats, position: Int, delegate: ItemDelegate<Any>) {
        itemView.txt_budget.text = item.budget.asCurrency
    }

    companion object {
        const val LAYOUT_TYPE: Int = R.layout.viewholder_budget_overview
    }
}