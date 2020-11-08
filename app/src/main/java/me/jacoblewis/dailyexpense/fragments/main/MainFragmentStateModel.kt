package me.jacoblewis.dailyexpense.fragments.main

import androidx.lifecycle.LiveData
import me.jacoblewis.dailyexpense.commons.CombinedLiveData
import me.jacoblewis.dailyexpense.data.models.Category
import me.jacoblewis.dailyexpense.data.models.NextDay
import me.jacoblewis.dailyexpense.data.models.PaymentCategory
import me.jacoblewis.dailyexpense.viewModels.CategoryViewModel
import me.jacoblewis.dailyexpense.viewModels.PaymentViewModel

data class MainFragmentStateModel(
        val payments: List<PaymentCategory>? = null,
        val remainingBudget: Float? = null,
        val categories: List<Category>? = null,
        val dailyPressure: Float? = null,
        val nextDayBalance: List<NextDay>? = null
) {
    companion object {
        fun mergeSources(paymentViewModel: PaymentViewModel,
                         categoryViewModel: CategoryViewModel): LiveData<MainFragmentStateModel> {
            val paymentsAndCategories = CombinedLiveData(paymentViewModel.payments, categoryViewModel.categories) { data1, data2 ->
                MainFragmentStateModel(payments = data1?.first, remainingBudget = data1?.second, categories = data2)
            }
            val dailyPressureAndNextDay = CombinedLiveData(paymentViewModel.dailyPressure, paymentViewModel.nextDayBalance) { data1, data2 ->
                MainFragmentStateModel(dailyPressure = data1, nextDayBalance = data2)
            }
            return CombinedLiveData(paymentsAndCategories, dailyPressureAndNextDay) { data1, data2 ->
                MainFragmentStateModel(payments = data1?.payments,
                        remainingBudget = data1?.remainingBudget,
                        categories = data1?.categories,
                        dailyPressure = data2?.dailyPressure,
                        nextDayBalance = data2?.nextDayBalance)
            }
        }
    }
}