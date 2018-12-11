package me.jacoblewis.dailyexpense.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment

@Database(entities = [Category::class, Payment::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BalancesDB : RoomDatabase() {
    abstract fun paymentsDao(): PaymentsDao
    abstract fun categoriesDao(): CategoriesDao

    companion object {
        val DATABASE_NAME = "balances_db"

        // For Singleton instantiation
        @Volatile
        private var instance: BalancesDB? = null

        private val migration: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE categories ADD COLUMN locked INTEGER NOT NULL DEFAULT 0")
            }

        }

        fun getInstance(context: Context): BalancesDB {
            return instance ?: synchronized(this) {
                instance
                        ?: Room.databaseBuilder(context, BalancesDB::class.java, DATABASE_NAME).addMigrations(migration).build().also { instance = it }
            }
        }
    }
}