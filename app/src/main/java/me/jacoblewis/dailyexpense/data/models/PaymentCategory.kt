package me.jacoblewis.dailyexpense.data.models

import androidx.room.Embedded
import androidx.room.Relation

class PaymentCategory {

    @Embedded
    var transaction: Payment? = null

    @Relation(parentColumn = "category_id", entityColumn = "id")
    var category: List<Category> = listOf()
}