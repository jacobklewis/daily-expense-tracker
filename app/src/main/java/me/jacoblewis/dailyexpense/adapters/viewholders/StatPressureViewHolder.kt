package me.jacoblewis.dailyexpense.adapters.viewholders

import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.dailyexpense.ui.components.MultiSectionThinPieChart
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder

class StatPressureViewHolder(viewGroup: ViewGroup) : RBRecyclerViewHolder<Stats, ItemDelegate<Any>>(viewGroup, LAYOUT_TYPE) {
    @BindView(R.id.chart_pressure)
    lateinit var pressureChart: MultiSectionThinPieChart

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun setUpView(itemView: View, item: Stats, position: Int, delegate: ItemDelegate<Any>) {

    }

    companion object {
        const val LAYOUT_TYPE: Int = R.layout.viewholder_pressure
    }
}