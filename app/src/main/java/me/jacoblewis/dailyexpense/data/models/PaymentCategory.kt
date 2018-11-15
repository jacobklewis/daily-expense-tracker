package me.jacoblewis.dailyexpense.data.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

class PaymentCategory {

    @Embedded
    var transaction: Payment? = null

    @Relation(parentColumn = "category_id", entityColumn = "id")
    var category: List<Category> = listOf()
}