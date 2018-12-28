package me.jacoblewis.dailyexpense.fragments.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import me.jacoblewis.dailyexpense.commons.DateHelper
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.managers.BalanceManager
import javax.inject.Inject

class MainViewModel
@Inject
constructor(val db: BalancesDB, val balanceManager: BalanceManager) : ViewModel() {

    val payments: LiveData<List<PaymentCategory>> = db.paymentsDao().getAllPaymentsSince(DateHelper.firstDayOfMonth())
    val dailyBalance: LiveData<Float> = balanceManager.fetchDailyBalance()

}