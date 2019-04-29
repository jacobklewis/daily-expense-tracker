package me.jacoblewis.dailyexpense.adapters.viewholders

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder

class BudgetOverviewViewHolder(viewGroup: ViewGroup) : RBRecyclerViewHolder<Stats, ItemDelegate<Any>>(viewGroup, LAYOUT_TYPE) {
    @BindView(R.id.txt_budget)
    lateinit var budgetTextView: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun setUpView(itemView: View, item: Stats, position: Int, delegate: ItemDelegate<Any>) {
        budgetTextView.text = item.budget.asCurrency
    }

    companion object {
        const val LAYOUT_TYPE: Int = R.layout.viewholder_budget_overview
    }
}