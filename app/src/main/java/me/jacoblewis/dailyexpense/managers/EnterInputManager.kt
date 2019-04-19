package me.jacoblewis.dailyexpense.managers

import androidx.lifecycle.MutableLiveData

class EnterInputManager(private var value: Double = 0.0) {
    val currentValue: MutableLiveData<Double> = MutableLiveData()

    init {
        applyValue(value)
    }

    private fun applyValue(v: Double) {
        value = v
        currentValue.postValue(v)
    }

    fun addDigit(digit: Int) {
        applyValue(value * 10 + digit / 100.0)
    }

    fun removeDigit() {
        applyValue(((value * 100).toLong() / 10) / 100.0)
    }
}