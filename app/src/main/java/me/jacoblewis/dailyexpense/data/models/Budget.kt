package me.jacoblewis.dailyexpense.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "budgets")
@Parcelize
data class Budget(
        @ColumnInfo(name = "amount")
        var amount: Float,

        // Year code: 2017, 2018, 2019, etc
        @ColumnInfo(name = "year")
        val year: Int,

        // Month Code (0-11)
        @ColumnInfo(name = "month")
        val month: Int,

        @PrimaryKey
        @ColumnInfo(name = "id")
        val budgetId: String = UUID.randomUUID().toString(),

        @ColumnInfo(name = "needsSync")
        var needsSync: Boolean = true
) : Parcelable