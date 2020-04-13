package me.jacoblewis.dailyexpense.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.asCurrency
import kotlin.math.cos
import kotlin.math.sin


/**
 * Created by Exper on 7/27/2014.
 */
class PressureChart : AnimationElementBlockView {
    var startColor: Int = Color.BLACK
    var middleColor: Int = Color.GRAY
    var endColor: Int = Color.WHITE
    var pressure: Float = 0.5f
    var aspect: Float = 3f
    var segments: Int = 9

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.PressureChart,
                0, 0)

        try {
            startColor = a.getColor(R.styleable.PressureChart_startColor, startColor)
            middleColor = a.getColor(R.styleable.PressureChart_middleColor, middleColor)
            endColor = a.getColor(R.styleable.PressureChart_endColor, endColor)
            pressure = a.getFloat(R.styleable.PressureChart_pressure, pressure)
            aspect = a.getFloat(R.styleable.PressureChart_aspect, aspect)
            segments = a.getInteger(R.styleable.PressureChart_segments, segments)
        } finally {
            a.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        setInset(true)
        setLineThickness(dp2px(2))
        mBoundsF = bounds

        val middleIndex = segments / 2 // Even segments will be slightly offset

        for (i in 0 until middleIndex) {
            mPaint[0].color = ColorUtils.blendARGB(startColor, middleColor, i.toFloat() / middleIndex)
            val offsetLeft = mBoundsF.width() * i / segments
            val offsetRight = mBoundsF.width() * (1 - (i + 1).toFloat() / segments)
            canvas.drawRect(mBoundsF.left + offsetLeft,
                    mBoundsF.top,
                    mBoundsF.right - offsetRight,
                    mBoundsF.bottom,
                    mPaint[0])
        }
        for (i in middleIndex until segments) {
            mPaint[0].color = ColorUtils.blendARGB(middleColor, endColor, (i.toFloat() - middleIndex) / (segments - middleIndex - 1))
            val offsetLeft = mBoundsF.width() * i / segments
            val offsetRight = mBoundsF.width() * (1 - (i + 1).toFloat() / segments)
            canvas.drawRect(mBoundsF.left + offsetLeft,
                    mBoundsF.top,
                    mBoundsF.right - offsetRight,
                    mBoundsF.bottom,
                    mPaint[0])
        }

        mPaint[0].color = Color.parseColor("#99000000")
        mPaint[1].style = Paint.Style.FILL_AND_STROKE
        mPaint[1].color = Color.parseColor("#99000000")
        mPaint[1].style = Paint.Style.STROKE

        if (pressure > 1f) {
            pressure = 1f
        }
        val offsetLeft = pressure * mBoundsF.width() - dp2px(10)
        val offsetRight = (1 - pressure) * mBoundsF.width() - dp2px(10)
        canvas.drawRect(mBoundsF.left + offsetLeft,
                mBoundsF.top + dp2px(10),
                mBoundsF.right - offsetRight,
                mBoundsF.bottom - dp2px(10),
                mPaint[0])
        canvas.drawRect(mBoundsF.left + offsetLeft,
                mBoundsF.top + dp2px(10),
                mBoundsF.right - offsetRight,
                mBoundsF.bottom - dp2px(10),
                mPaint[1])

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthPixels = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val adjustedHeight = (widthPixels / aspect).toInt()
        val adjHeightMeasureSpec = MeasureSpec.makeMeasureSpec(adjustedHeight, widthMode)
        super.onMeasure(widthMeasureSpec, adjHeightMeasureSpec)
    }

    //
//    fun animateThis(totAmount: Int, time: Int) {
//        totalAmount = totAmount.toFloat()
//        val t = Thread(Runnable {
//            val a = object : Animation() {
//                @Throws(CloneNotSupportedException::class)
//                override fun clone(): Animation {
//                    return super.clone()
//                }
//
//                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
//                    percentComplete = interpolatedTime
//                    requestLayout()
//                    invalidate()
//                }
//            }
//            a.interpolator = AccelerateDecelerateInterpolator()
//            a.duration = time.toLong()
//            a.startOffset = 500
//            try {
//                startAnimation(a)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        })
//        t.start()
//
//    }
}
