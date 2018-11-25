package me.jacoblewis.dailyexpense.fragments.main

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.Payment
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import javax.inject.Inject

class MainViewModel
@Inject
constructor(val db: BalancesDB) : ViewModel() {

    val payments = MediatorLiveData<List<PaymentCategory>>()

    init {
        payments.addSource(db.paymentsDao().getAllPayments(), payments::setValue)
    }

    // TODO: remove and add to add payment screen
    fun addMockPayment() {
        val mockedPayment = Payment(cost = (Math.random() * 15000 / 100).toFloat(), notes = "")
        GlobalScope.launch {
            db.paymentsDao().insertPayment(mockedPayment)
        }

    }
}