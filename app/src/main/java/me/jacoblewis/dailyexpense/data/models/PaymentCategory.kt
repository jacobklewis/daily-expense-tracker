package me.jacoblewis.dailyexpense.data.models

import androidx.room.Embedded
import androidx.room.Relation
import me.jacoblewis.jklcore.components.recyclerview.IdItem

class PaymentCategory : IdItem<Long> {

    @Embedded
    var transaction: Payment? = null

    @Relation(parentColumn = "category_id", entityColumn = "id")
    var category: List<Category> = listOf()

    override fun getIdentifier(): Long = transaction?.identifier ?: 0
}