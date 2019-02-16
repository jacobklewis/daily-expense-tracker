package me.jacoblewis.dailyexpense.data.models

import me.jacoblewis.dailyexpense.commons.StatsType
import me.jacoblewis.jklcore.components.recyclerview.IdItem
import java.util.*

data class Stats(val budget: Float,
                 val categories: List<Category> = listOf(),
                 val displayType: StatsType = StatsType.Overview) : IdItem<String> {
    override fun getIdentifier(): String = UUID.randomUUID().toString()
}