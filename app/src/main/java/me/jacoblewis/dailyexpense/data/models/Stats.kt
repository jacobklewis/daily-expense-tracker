package me.jacoblewis.dailyexpense.data.models

import me.jacoblewis.jklcore.components.recyclerview.IdItem
import java.util.*

data class Stats(val budget: Float) : IdItem<String> {
    override fun getIdentifier(): String = UUID.randomUUID().toString()
}