package me.jacoblewis.dailyexpense.fragments.categories

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment
import javax.inject.Inject

class CategoryViewModel
@Inject
constructor(val db: BalancesDB) : ViewModel() {

    val categories = MediatorLiveData<List<Category>>()

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
}