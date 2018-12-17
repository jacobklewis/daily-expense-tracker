package me.jacoblewis.dailyexpense.fragments.categories

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.commons.fromCurrency
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment
import javax.inject.Inject

class CategoryViewModel
@Inject
constructor(val db: BalancesDB, val sp: SharedPreferences) : ViewModel() {

    val categories = MediatorLiveData<List<Category>>()
    val budget = sp.getString("budget", "0")?.fromCurrency ?: 0f

    init {
        categories.addSource(db.categoriesDao().getAllCategories(), categories::setValue)
    }

    fun updateCategories(categories: List<Category>) {
        GlobalScope.launch {
            db.categoriesDao().updateCategories(categories)
        }
    }

    fun savePayment(payment: Payment) {
        GlobalScope.launch {
            db.paymentsDao().insertPayment(payment)
        }
    }

    fun removeCategory(category: Category) {
        GlobalScope.launch {
            try {
                db.paymentsDao().deleteByCategory(categoryId = category.categoryId)
                db.categoriesDao().deleteCategory(category)
            } catch (e: Exception) {
                // TODO: log somewhere else
                Log.e("CategoryViewModel", e.localizedMessage)
            }
        }
    }
}