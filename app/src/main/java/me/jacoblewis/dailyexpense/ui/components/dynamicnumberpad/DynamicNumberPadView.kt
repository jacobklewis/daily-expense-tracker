package me.jacoblewis.dailyexpense.ui.components.dynamicnumberpad

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.ui.components.AnimationElementBlockView
import me.jacoblewis.dailyexpense.ui.components.dynamicnumberpad.models.DynamicNumberPadModel

class DynamicNumberPadView(context: Context, attrs: AttributeSet? = null) : AnimationElementBlockView(context, attrs) {

    val model = DynamicNumberPadModel()
    val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val currentNumber: Float
        get() = model.vertConstraintValue + model.horizConstraintValue / 100f


    init {
        setColors(intArrayOf(Color.CYAN, Color.BLUE, Color.GRAY), intArrayOf(Color.MAGENTA, Color.RED, Color.YELLOW))
        textPaint.color = Color.WHITE
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 16.dp

        viewTreeObserver.addOnGlobalLayoutListener {
            model.setup(bounds, model.position)
            model.bounds.inset(15.dp, 10.dp)
        }
    }


    override fun draw(canvas: Canvas) {
        canvas.drawRect(bounds, mPaint[2])
        canvas.drawCircle(model.position.x, model.position.y, 20.dp, mPaintWhite)
        printLabel(canvas, currentNumber.asCurrency, model.position.x, model.position.y - 75.dp, textPaint)
        super.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            model.touchState = MotionEvent.ACTION_DOWN
            return true
        } else if (event.action == MotionEvent.ACTION_MOVE && model.touchState == MotionEvent.ACTION_DOWN) {
            model.position.set(PointF(event.x, event.y))
            invalidate()
            requestLayout()
            return true
        } else if (event.action == MotionEvent.ACTION_UP && model.touchState == MotionEvent.ACTION_DOWN) {
            model.touchState = MotionEvent.ACTION_UP
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }
}