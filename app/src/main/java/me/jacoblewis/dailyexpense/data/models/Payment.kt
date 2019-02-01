package me.jacoblewis.dailyexpense.data.models

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import me.jacoblewis.jklcore.components.recyclerview.IdItem
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

        @PrimaryKey
        @ColumnInfo(name = "id")
        var id: String = UUID.randomUUID().toString(),

        @ColumnInfo(name = "category_id")
        var categoryId: String = "",

        @ColumnInfo(name = "needsSync")
        var needsSync: Boolean = true,

        @ColumnInfo(name = "deleted")
        var deleted: Boolean = false
) : Parcelable, IdItem<String> {
    override fun getIdentifier(): String = id
}