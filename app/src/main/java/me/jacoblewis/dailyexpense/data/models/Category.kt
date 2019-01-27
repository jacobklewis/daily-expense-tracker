package me.jacoblewis.dailyexpense.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import me.jacoblewis.jklcore.components.recyclerview.IdItem

@Entity(tableName = "categories")
data class Category(
        @ColumnInfo(name = "name")
        val name: String,

        @ColumnInfo(name = "color")
        val color: String,

        // Float 0-1 based on relative ratio
        @ColumnInfo(name = "category_budget")
        var budget: Float = 0f,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var categoryId: Long = 0
) : IdItem<Long> {
    var locked: Boolean = false

    @Ignore
    val payments: MutableList<Payment> = mutableListOf()

    override fun getIdentifier(): Long = categoryId
}