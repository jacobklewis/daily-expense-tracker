package me.jacoblewis.dailyexpense.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import me.jacoblewis.dailyexpense.data.daos.BudgetsDao
import me.jacoblewis.dailyexpense.data.daos.CategoriesDao
import me.jacoblewis.dailyexpense.data.daos.PaymentsDao
import me.jacoblewis.dailyexpense.data.models.Budget
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment

@Database(entities = [Category::class, Payment::class, Budget::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BalancesDB : RoomDatabase() {
    abstract fun paymentsDao(): PaymentsDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun budgetsDao(): BudgetsDao

    companion object {
        val DATABASE_NAME = "balances_db"

        // For Singleton instantiation
        @Volatile
        private var instance: BalancesDB? = null

        private val migration: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migrate Categories
                database.execSQL("ALTER TABLE categories RENAME TO categories_temp;")
                database.execSQL("CREATE TABLE categories(name TEXT NOT NULL, color TEXT NOT NULL, notes TEXT NOT NULL DEFAULT \"\", id TEXT NOT NULL PRIMARY KEY, needsSync INTEGER NOT NULL DEFAULT 1, deleted INTEGER NOT NULL DEFAULT 0);")
                database.execSQL("INSERT INTO categories(name, color, id) SELECT name, color, id FROM categories_temp;")
                database.execSQL("DROP TABLE categories_temp;")

                // Migrate Payments
                database.execSQL("ALTER TABLE payments RENAME TO payments_temp;")
                database.execSQL("DROP INDEX index_payments_category_id;")
                database.execSQL("CREATE TABLE payments(cost REAL NOT NULL, creation_date INTEGER NOT NULL, notes TEXT NOT NULL, id TEXT NOT NULL PRIMARY KEY, category_id TEXT NOT NULL, needsSync INTEGER NOT NULL DEFAULT 1, deleted INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(category_id) REFERENCES categories(id) ON UPDATE NO ACTION ON DELETE NO ACTION);")
                database.execSQL("INSERT INTO payments(cost, creation_date, notes, category_id, id) SELECT cost, creation_date, notes, category_id, id FROM payments_temp;")
                database.execSQL("CREATE INDEX index_payments_category_id on payments (category_id);")
                database.execSQL("DROP TABLE payments_temp;")

                // Create Budgets
                database.execSQL("ALTER TABLE budgets RENAME TO budgets_temp;")
                database.execSQL("CREATE TABLE budgets(amount REAL NOT NULL, year INTEGER NOT NUlL, month INTEGER NOT NULL, id TEXT NOT NULL PRIMARY KEY, needsSync INTEGER NOT NULL DEFAULT 1)")
                database.execSQL("INSERT INTO budgets(amount, year, month, id) SELECT amount, year, month, id FROM budgets_temp;")
                database.execSQL("DROP TABLE budgets_temp;")
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