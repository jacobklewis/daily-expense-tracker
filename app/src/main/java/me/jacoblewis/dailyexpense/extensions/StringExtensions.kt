package me.jacoblewis.dailyexpense.extensions

import android.graphics.Color

val String.asColorInt: Int
    get() = if (this.contains("#")) Color.parseColor(this) else Color.parseColor("#$this")