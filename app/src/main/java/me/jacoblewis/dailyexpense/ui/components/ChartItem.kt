package me.jacoblewis.dailyexpense.ui.components

import androidx.annotation.ColorInt

data class ChartItem(val value: Number,
                     @ColorInt val color: Int,
                     val label: String = "",
                     val subLabel: String = "")