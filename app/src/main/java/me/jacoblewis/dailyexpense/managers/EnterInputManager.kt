package me.jacoblewis.dailyexpense.managers

import androidx.lifecycle.MutableLiveData

class EnterInputManager(private var value: Float = 0f) {
    val currentValue: MutableLiveData<Float> = MutableLiveData()

    init {
        applyValue(value)
    }

    private fun applyValue(v: Float) {
        value = v
        currentValue.postValue(v)
    }

    fun addDigit(digit: Int) {
        applyValue(value * 10 + digit / 100f)
    }

    fun removeDigit() {
        applyValue(((value * 100).toLong() / 10) / 100f)
    }
}