package me.jacoblewis.dailyexpense.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import me.jacoblewis.dailyexpense.managers.EnterInputManager
import javax.inject.Inject

class EnterPaymentViewModel
@Inject
constructor() : ViewModel() {
    private val enterInputManager = EnterInputManager()
    val finalInput: LiveData<Double> = enterInputManager.currentValue

    fun addDigit(digit: Int) {
        enterInputManager.addDigit(digit)
    }

    fun removeDigit() {
        enterInputManager.removeDigit()
    }
}