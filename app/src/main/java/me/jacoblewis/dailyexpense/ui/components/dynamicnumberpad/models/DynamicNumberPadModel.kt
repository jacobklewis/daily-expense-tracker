package me.jacoblewis.dailyexpense.ui.components.dynamicnumberpad.models

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent

class DynamicNumberPadModel {
    val bounds: RectF = RectF(0f, 0f, 0f, 0f)
    val position: PointF = PointF(0f, 0f)
    val constraints: Rect = Rect(0, 0, 100, 100)
    var touchState: Int = MotionEvent.ACTION_UP

    fun setup(bounds: RectF, position: PointF) {
        this.bounds.set(bounds)
        this.position.set(position)
    }

    val horizConstraintValue: Int
        get() = (position.x / bounds.width() * constraints.width()).toInt()
    val vertConstraintValue: Int
        get() = ((bounds.height() - position.y) / bounds.height() * constraints.height()).toInt()
}