package me.jacoblewis.dailyexpense.fragments.categories

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment
import javax.inject.Inject

class SettingsViewModel
@Inject
constructor(val db: BalancesDB) : ViewModel() {

    val budget = MediatorLiveData<Float>()

    init {
//        budget.addSource(TODO(), budget::setValue)
    }
}