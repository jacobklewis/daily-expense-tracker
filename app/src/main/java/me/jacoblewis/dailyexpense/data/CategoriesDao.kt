package me.jacoblewis.dailyexpense.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import me.jacoblewis.dailyexpense.data.models.Category

@Dao
interface CategoriesDao {

    @Query("SELECT * FROM categories")
    fun getAllCategories(): LiveData<List<Category>>

    @Update
    fun updateCategories(categories: List<Category>)

    @Insert
    fun insertCategory(category: Category): Long
}