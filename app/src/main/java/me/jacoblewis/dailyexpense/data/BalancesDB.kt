package me.jacoblewis.dailyexpense.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment

@Database(entities = [Category::class, Payment::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BalancesDB : RoomDatabase() {
    abstract fun paymentsDao(): PaymentsDao
    abstract fun categoriesDao(): CategoriesDao

    companion object {
        val DATABASE_NAME = "balances_db"

        // For Singleton instantiation
        @Volatile
        private var instance: BalancesDB? = null

        fun getInstance(context: Context): BalancesDB {
            return instance ?: synchronized(this) {
                instance
                        ?: Room.databaseBuilder(context, BalancesDB::class.java, DATABASE_NAME).build().also { instance = it }
            }
        }
    }
}