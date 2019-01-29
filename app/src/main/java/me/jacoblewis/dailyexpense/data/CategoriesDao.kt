package me.jacoblewis.dailyexpense.data

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

    @Update
    fun updateCategories(categories: List<Category>)

    @Update
    fun updateCategory(category: Category)

    @Insert
    fun insertCategory(category: Category): Long

    @Delete
    fun deleteCategory(category: Category)
}