package me.jacoblewis.dailyexpense.adapters.viewholders

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.ItemDelegate
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.data.models.Stats
import me.jacoblewis.dailyexpense.ui.components.ChartItem
import me.jacoblewis.dailyexpense.ui.components.MultiSectionThinPieChart
import me.jacoblewis.jklcore.components.recyclerview.RBRecyclerViewHolder
import kotlin.random.Random

class StatPieViewHolder(viewGroup: ViewGroup) : RBRecyclerViewHolder<Stats, ItemDelegate<Any>>(viewGroup, R.layout.viewholder_pie_dist) {
    @BindView(R.id.chart_pie)
    lateinit var pieChart: MultiSectionThinPieChart

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun setUpView(itemView: View, item: Stats, position: Int, delegate: ItemDelegate<Any>) {
        val overallSum = item.categories.flatMap { it.payments.map { p -> p.cost } }.sum()
        pieChart.items = item.categories.map {
            val cost = it.payments.map { p -> p.cost }.sum()
//            val color = if (it.color.contains("#")) Color.parseColor(it.color) else Color.parseColor("#${it.color}")
            val color = Color.rgb(Random.nextInt(0,255), Random.nextInt(0,255), Random.nextInt(0,255))
            ChartItem(cost, color, it.name, cost.asCurrency)
        }.filter { it.value.toFloat() / overallSum > 0.075f }
    }
}