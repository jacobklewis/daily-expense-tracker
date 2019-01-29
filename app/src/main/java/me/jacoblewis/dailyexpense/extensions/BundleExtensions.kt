package me.jacoblewis.dailyexpense.extensions

import android.os.Bundle
import android.os.Parcelable

inline fun <reified T : Parcelable> Bundle.applyModel(block: () -> T): Bundle {
    putParcelable(T::class.java.name, block())
    return this
}