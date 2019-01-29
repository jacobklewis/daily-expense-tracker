package me.jacoblewis.dailyexpense.data.models

import androidx.room.Embedded
import androidx.room.Relation

class CategoryPayments {

    @Embedded
    lateinit var category: Category

    @Relation(parentColumn = "id", entityColumn = "category_id")
    var payments: List<Payment> = listOf()
}