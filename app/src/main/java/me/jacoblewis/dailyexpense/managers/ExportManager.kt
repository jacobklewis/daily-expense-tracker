package me.jacoblewis.dailyexpense.managers

import android.content.Context
import android.util.Log
import androidx.annotation.UiThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.commons.transposeStrict
import me.jacoblewis.dailyexpense.data.daos.PaymentsDao
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.data.models.export.ExportCategory
import me.jacoblewis.dailyexpense.data.models.export.ExportMonth
import java.util.*
import javax.inject.Inject

class ExportManager
@Inject constructor(val context: Context, val paymentsDao: PaymentsDao, val appScope: CoroutineScope) {

    @UiThread
    fun exportNow() {
        appScope.launch(Dispatchers.IO) {
            val allPayments = paymentsDao.getAllPaymentsNow()
            val exportData = gatherExportData(allPayments)
            exportData.forEach {
                Log.e("exportData for ${it.month} - ${it.year}", "\n${convertToCSV(it)}")
            }
        }
    }

    fun gatherExportData(paymentCategories: List<PaymentCategory>): List<ExportMonth> {
        val groupedPaymentDates = paymentCategories.groupBy {
            val cal = it.transaction?.creationDate ?: return@groupBy Pair("NA", "NA")
            val month = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)
            Pair("$month", "$year")
        }
        return groupedPaymentDates.map { (date, items) ->
            val groupedCats = items.groupBy { it.category.first().name }
            val exportCats = groupedCats.map { (cat, payments) ->
                ExportCategory(cat, payments.mapNotNull { it.transaction?.cost?.asCurrency })
            }
            ExportMonth(date.first, date.second, exportCats)
        }
    }

    fun convertToCSV(exportData: ExportMonth): String {
        val rotatedData = transposeStrict(exportData.exportCategories.sortedBy { it.name }.map { listOf(it.name) + it.costs })
        return rotatedData.joinToString("\n") { it.joinToString(",") { s -> s ?: "" } }
    }
}