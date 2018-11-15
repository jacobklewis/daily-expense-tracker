package me.jacoblewis.dailyexpense.data.models

import android.arch.persistence.room.*
import java.util.*

@Entity(tableName = "payments",
        foreignKeys = [ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category_id"])],
        indices = [Index("category_id")])
data class Payment(
        @ColumnInfo(name = "category_id")
        val categoryId: Long,

        @ColumnInfo(name = "creation_date")
        val creationDate: Calendar = Calendar.getInstance(),

        @ColumnInfo(name = "cost")
        val cost: Float,

        @ColumnInfo(name = "notes")
        val notes: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}