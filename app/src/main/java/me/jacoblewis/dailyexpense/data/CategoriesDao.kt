package me.jacoblewis.dailyexpense.data

import androidx.lifecycle.LiveData
import androidx.room.*
import me.jacoblewis.dailyexpense.data.models.Category

@Dao
interface CategoriesDao {

    @Query("SELECT * FROM categories")
    fun getAllCategories(): LiveData<List<Category>>

    @Update
    fun updateCategories(categories: List<Category>)

    @Insert
    fun insertCategory(category: Category): Long

    @Delete
    fun deleteCategory(category: Category)
}