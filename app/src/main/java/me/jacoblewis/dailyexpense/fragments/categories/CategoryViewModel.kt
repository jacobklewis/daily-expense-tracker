package me.jacoblewis.dailyexpense.fragments.categories

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Category
import javax.inject.Inject

class CategoryViewModel
@Inject
constructor(val db: BalancesDB) : ViewModel() {

    val categories = MediatorLiveData<List<Category>>()

    init {
        categories.addSource(db.categoriesDao().getAllCategories(), categories::setValue)
    }
}