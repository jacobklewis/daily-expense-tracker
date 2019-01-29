package me.jacoblewis.dailyexpense.data.models

import me.jacoblewis.jklcore.components.recyclerview.IdItem

data class Stats(val budget: Float) : IdItem<Long> {
    override fun getIdentifier(): Long = 0
}