package me.jacoblewis.dailyexpense.data.daos

import androidx.annotation.WorkerThread
import androidx.room.*
import me.jacoblewis.dailyexpense.data.models.Budget

@Dao
interface BudgetsDao {
    @Insert
    fun insertBudget(budget: Budget)

    @WorkerThread
    @Insert
    fun insertBudgets(budgets: List<Budget>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateBudget(budget: Budget)

    @Query("SELECT * FROM budgets WHERE year = :year AND month = :month LIMIT 1")
    fun getBudgetForMonth(year: Int, month: Int): Budget?

    @Query("SELECT * FROM budgets ORDER BY year, month DESC LIMIT 1")
    fun getLastBudget(): Budget?

    @WorkerThread
    @Query("SELECT * FROM budgets WHERE needsSync = 1")
    fun getAllToSync(): List<Budget>

    @WorkerThread
    @Query("UPDATE budgets SET needsSync = 0")
    fun setAllSync()

    @WorkerThread
    @Query("DELETE FROM budgets")
    fun deleteAll()
}