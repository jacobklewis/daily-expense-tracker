package me.jacoblewis.dailyexpense.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.asCurrency
import kotlin.math.cos
import kotlin.math.sin


/**
 * Created by Exper on 7/27/2014.
 */
class MultiSectionThinPieChart : AnimationElementBlockView {
    private var percentComplete = 1f
    private var totalAmount = 100f
    private var title: String? = null
    var items: List<ChartItem> = listOf(ChartItem(40, Color.BLUE, "Blue"), ChartItem(70, Color.GREEN, "Green", 7_550.asCurrency))
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ThinPieChart,
                0, 0)

        mPaintWhite.textAlign = Paint.Align.CENTER
        mPaintWhite.textSize = dp2px(15)

        try {
            title = a.getString(R.styleable.ThinPieChart_title)
            if (title == null) {
                title = ""
            }
        } finally {
            a.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        totalAmount = items.map { it.value.toFloat() }.sum()
        setInset(true)
        setLineThickness(dp2px(30))
        mBoundsF = bounds

        mPaint[0].color = Color.WHITE

        mPaint[0].textAlign = Paint.Align.CENTER
        mPaint[0].textSize = mBoundsF.width() / 5
        mPaint[0].isFakeBoldText = true

        mPaintWhite.style = Paint.Style.STROKE
        canvas.drawOval(mBoundsF, mPaintWhite)

        var drawTo = -90f
        var drawFrom = -90f
        for (i in items.indices) {
            mPaint[1].color = items[i].color
            mPaint[1].strokeWidth = dp2px(30)
            drawTo += 360f * percentComplete * (items[i].value.toFloat() / totalAmount) + 0.2f
            mPaint[1].style = Paint.Style.STROKE
            canvas.drawArc(mBoundsF, drawFrom, drawTo - drawFrom, false, mPaint[1])
            val mid = (drawFrom + drawTo) / 2
            val x = bounds.centerX() + bounds.centerX() / 2f * cos(mid * Math.PI / 180)
            val y = bounds.centerY() + bounds.centerY() / 1.4f * sin(mid * Math.PI / 180)
            if (items[i].subLabel.isBlank()) {
                printLabel(canvas, items[i].label, x.toFloat(), y.toFloat(), mPaintWhite)
            } else {
                printLabel(canvas, items[i].label, items[i].subLabel, x.toFloat(), y.toFloat(), mPaintWhite)
            }
            drawFrom = drawTo - 0.2f // We need a little overlap for a better display
        }


//        mPaint[0].style = Paint.Style.FILL
//        canvas.drawText((if (amounts[1] == 0) "" else ">") + amount + "", mBoundsF.centerX(), mBoundsF.centerY() * 3 / 5 + mBoundsF.width() / 8, mPaint[0])
//        mPaint[0].color = resources.getColor(R.color.black)
//        canvas.drawText(total, mBoundsF.centerX(), mBoundsF.centerY() * 8 / 5 - mBoundsF.width() / 16, mPaint[0])
//        mPaint[0].textSize = mBoundsF.width() / 10
//        canvas.drawText("OUT OF", mBoundsF.centerX(), mBoundsF.centerY() + mBoundsF.width() / 20, mPaint[0])
//        mPaint[0].textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, 6f, resources.displayMetrics)
//        mPaint[0].color = resources.getColor(R.color.colorPrimary)
//        canvas.drawText(title!!, mBoundsF.centerX(), mBoundsF.height() - mBoundsF.width() / 20, mPaint[0])

//        mPaint[0].color = colors[1].color
//        canvas.drawCircle(mBoundsF.width() - dp2px(10), dp2px(23), dp2px(15), mPaint[0])
//        mPaint[0].color = resources.getColor(R.color.white)
//        mPaint[0].textSize = dp2px(20)
//        canvas.drawText("?", mBoundsF.width() - dp2px(10), dp2px(30), mPaint[0])

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.EXACTLY))
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
