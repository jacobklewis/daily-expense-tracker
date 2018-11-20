package me.jacoblewis.dailyexpense.data.models

import android.arch.persistence.room.*
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "payments",
        foreignKeys = [ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category_id"])],
        indices = [Index("category_id")])
@Parcelize
data class Payment(
        @ColumnInfo(name = "creation_date")
        val creationDate: Calendar = Calendar.getInstance(),

        @ColumnInfo(name = "cost")
        val cost: Float,

        @ColumnInfo(name = "notes")
        val notes: String
): Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "category_id")
    var categoryId: Long = 0
}