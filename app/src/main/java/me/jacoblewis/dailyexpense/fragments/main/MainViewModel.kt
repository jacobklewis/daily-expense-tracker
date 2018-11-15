package me.jacoblewis.dailyexpense.fragments.main

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import javax.inject.Inject

class MainViewModel
@Inject
constructor(val db: BalancesDB) : ViewModel() {

    val payments = MediatorLiveData<List<PaymentCategory>>()

    init {
        payments.addSource(db.paymentsDao().getAllPayments(), payments::setValue)

        db.categoriesDao().getAllCategories().observeForever {
            if (it != null && it.isEmpty()) {

                GlobalScope.launch {
                    Log.e("category id", "${db.categoriesDao().insertCategory(Category("Lunch", "1B5E20", 230.00f))}")
                }
            } else {
                Log.e("Cat", "Already have one!!! :D")
            }
        }
    }

    // TODO: remove and add to add payment screen
    fun addMockPayment() {
        val mockedPayment = Payment(1, cost = (Math.random()*15000/100).toFloat(), notes = "")
        GlobalScope.launch {
            db.paymentsDao().insertPayment(mockedPayment)
        }

    }
}