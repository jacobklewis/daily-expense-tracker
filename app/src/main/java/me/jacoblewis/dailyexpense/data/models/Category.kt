package me.jacoblewis.dailyexpense.data.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
        @ColumnInfo(name = "name")
        val name: String,

        @ColumnInfo(name = "color")
        val color: String,

        @ColumnInfo(name = "category_budget")
        val budget: Float
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var categoryId: Long = 0
}