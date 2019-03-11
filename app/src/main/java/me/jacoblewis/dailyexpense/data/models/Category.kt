package me.jacoblewis.dailyexpense.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import me.jacoblewis.jklcore.components.recyclerview.IdItem
import java.util.*

@Entity(tableName = "categories")
@Parcelize
data class Category(
        @ColumnInfo(name = "name")
        var name: String,

        @ColumnInfo(name = "color")
        var color: String,

        @ColumnInfo(name = "notes")
        val notes: String = "",

        @PrimaryKey
        @ColumnInfo(name = "id")
        var categoryId: String = UUID.randomUUID().toString(),

        @ColumnInfo(name = "needsSync")
        var needsSync: Boolean = true,

        @ColumnInfo(name = "deleted")
        var deleted: Boolean = false
) : IdItem<String>, Parcelable {
    @Ignore
    val payments: MutableList<Payment> = mutableListOf()

    val totalCost: Float
        get() = payments.map { p -> p.cost }.sum()

    override fun equals(other: Any?): Boolean {
        return other is Category && payments == other.payments && super.equals(other)
    }

    override fun getIdentifier(): String = categoryId
}