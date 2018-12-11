package me.jacoblewis.dailyexpense.data.models

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "payments",
        foreignKeys = [ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category_id"])],
        indices = [Index("category_id")])
@Parcelize
data class Payment(
        @ColumnInfo(name = "cost")
        val cost: Float,

        @ColumnInfo(name = "creation_date")
        val creationDate: Calendar = Calendar.getInstance(),

        @ColumnInfo(name = "notes")
        val notes: String = "",

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Long = 0,

        @ColumnInfo(name = "category_id")
        var categoryId: Long = 0
) : Parcelable