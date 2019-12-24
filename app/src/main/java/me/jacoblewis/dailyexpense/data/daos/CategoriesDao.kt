package me.jacoblewis.dailyexpense.data.daos

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.*
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.CategoryPayments

@Dao
interface CategoriesDao {

    @Query("SELECT * FROM categories")
    fun getAllCategories(): LiveData<List<Category>>

    @Transaction
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategoryPayments(): LiveData<List<CategoryPayments>>

    @WorkerThread
    @Query("SELECT * FROM categories WHERE needsSync = 1")
    fun getAllToSync(): List<Category>

    @WorkerThread
    @Update
    fun updateCategories(categories: List<Category>)

    @WorkerThread
    @Update
    fun updateCategory(category: Category)

    @WorkerThread
    @Insert
    fun insertCategory(category: Category): Long

    @WorkerThread
    @Insert
    fun insertCategories(categories: List<Category>)

    @WorkerThread
    @Delete
    fun deleteCategory(category: Category)

    @WorkerThread
    @Query("UPDATE categories SET needsSync = 0")
    fun setAllSync()

    @WorkerThread
    @Query("DELETE FROM categories")
    fun deleteAll()
}