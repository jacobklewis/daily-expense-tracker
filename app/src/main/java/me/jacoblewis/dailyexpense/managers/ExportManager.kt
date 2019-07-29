package me.jacoblewis.dailyexpense.managers

import android.content.Context
import androidx.annotation.UiThread
import kotlinx.coroutines.CoroutineScope
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.daos.PaymentsDao
import javax.inject.Inject

class ExportManager
@Inject constructor(val context: Context, val paymentsDao: PaymentsDao, val appScope: CoroutineScope) {

    @UiThread
    fun exportNow() {

        val allPayments = paymentsDao.getAllPaymentsNow()
    }
}