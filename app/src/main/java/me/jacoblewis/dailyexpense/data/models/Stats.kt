package me.jacoblewis.dailyexpense.data.models

import me.jacoblewis.dailyexpense.commons.StatsType
import me.jacoblewis.jklcore.components.recyclerview.IdItem
import java.util.*

data class Stats(val budget: Float,
                 val categories: List<Category> = listOf(),
                 val displayType: StatsType = StatsType.Overview,
                 val pressure: Float = 0.5f,
                 val unique: Boolean = false,
                 val date: Date = Date()) : IdItem<String> {
    override fun getIdentifier(): String = if (unique) UUID.randomUUID().toString() else "STAT"
    override fun getHash(): Int = hashCode()
}