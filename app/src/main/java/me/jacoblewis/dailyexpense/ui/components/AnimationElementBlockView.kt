package me.jacoblewis.dailyexpense.ui.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import me.jacoblewis.dailyexpense.R


/**
 * Created by Exper on 7/13/2014.
 */
@UiThread
abstract class AnimationElementBlockView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    internal var mPaint = arrayOf(Paint(Paint.ANTI_ALIAS_FLAG), Paint(Paint.ANTI_ALIAS_FLAG), Paint(Paint.ANTI_ALIAS_FLAG))
    internal var mPaintSec = arrayOf(Paint(Paint.ANTI_ALIAS_FLAG), Paint(Paint.ANTI_ALIAS_FLAG), Paint(Paint.ANTI_ALIAS_FLAG))
    internal var mPaintBlack = Paint(Paint.ANTI_ALIAS_FLAG)
    internal var mPaintWhite = Paint(Paint.ANTI_ALIAS_FLAG)
    lateinit var mBoundsF: RectF
    private var isInset = false

    internal var text1r = Rect()
    internal var text2r = Rect()
    internal var hcorn = RectF()
    internal var label_bg = Path()

    private val density: Float

    internal val bounds: RectF
        get() {
            mBoundsF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val halfBorder = (mPaint[0].strokeWidth / 2f + 0.5f).toInt()
            if (isInset)
                mBoundsF.inset(halfBorder.toFloat(), halfBorder.toFloat())
            return mBoundsF
        }

    init {
        density = context.resources.displayMetrics.density

        val c1 = ContextCompat.getColor(context, R.color.black)
        val c2 = ContextCompat.getColor(context, R.color.white)

        mPaintBlack.color = c1
        mPaintWhite.color = c2

        setColors(intArrayOf(c1, c1, c1), intArrayOf(c1, c2, c2))
    }

    fun setColors(priColor: IntArray, secColor: IntArray) {
        for (i in 0..2) {
            mPaint[i].color = priColor[i]
            mPaintSec[i].color = secColor[i]
        }
        requestLayout()
        invalidate()
    }

    fun setLineThickness(thickness: Float) {
        mPaintWhite.strokeWidth = thickness
        mPaintBlack.strokeWidth = thickness

        for (i in 0..2) {
            mPaint[i].strokeWidth = thickness
            mPaintSec[i].strokeWidth = thickness
        }
    }

    internal fun dp2px(dp: Float): Float {
        return dp * density
    }

    internal val Number.dp: Float
        get() = dp2px(this.toFloat())

    internal fun setInset(insetVal: Boolean) {
        isInset = insetVal
    }

    internal class SwiftInOut : Interpolator {

        override fun getInterpolation(v: Float): Float {
            return 1 - Math.pow(Math.cos(Math.PI * v) / 2 + 0.5, (3 * v).toDouble()).toFloat()
        }
    }

    /**
     * Helper functions
     */
    internal fun printLabel(canvas: Canvas, t1: String, t2: String, tx: Float, ty: Float, textColor: Paint) {
        textColor.getTextBounds(t1, 0, t1.length - 1, text1r)
        textColor.getTextBounds(t2, 0, t2.length - 1, text2r)

        text1r.offset((tx - text1r.centerX()).toInt(), ty.toInt())
        text1r.inset((-textColor.textSize).toInt(), (-textColor.textSize).toInt() / 2)
        text2r.offset((tx - text2r.centerX()).toInt(), (ty + textColor.textSize * 3 / 2).toInt())
        text2r.inset((-textColor.textSize).toInt(), (-textColor.textSize).toInt() / 2)

        with(label_bg) {
            reset()
            moveTo(text1r.centerX().toFloat(), text1r.top.toFloat())
            lineTo((text1r.right - text1r.height() / 8).toFloat(), text1r.top.toFloat())
            hcorn.set((text1r.right - text1r.height() / 4).toFloat(), text1r.top.toFloat(), text1r.right.toFloat(), (text1r.top + text1r.height() / 4).toFloat())
            arcTo(hcorn, 270f, 90f)
            lineTo(text1r.right.toFloat(), (text1r.bottom - text1r.height() / 8).toFloat())
            hcorn.set((text1r.right - text1r.height() / 4).toFloat(), (text1r.bottom - text1r.height() / 4).toFloat(), text1r.right.toFloat(), text1r.bottom.toFloat())
            arcTo(hcorn, 0f, 90f)
        }

        label_bg.lineTo((text2r.right + text1r.height() / 4).toFloat(), text1r.bottom.toFloat())
        hcorn.set(text2r.right.toFloat(), text1r.bottom.toFloat(), (text2r.right + text2r.height() / 2).toFloat(), (text1r.bottom + text2r.height() / 2).toFloat())
        label_bg.arcTo(hcorn, 270f, -90f)
        label_bg.lineTo(text2r.right.toFloat(), (text2r.bottom - text2r.height() / 8).toFloat())
        hcorn.set((text2r.right - text2r.height() / 4).toFloat(), (text2r.bottom - text2r.height() / 4).toFloat(), text2r.right.toFloat(), text2r.bottom.toFloat())
        label_bg.arcTo(hcorn, 0f, 90f)
        label_bg.lineTo((text2r.left + text2r.height() / 8).toFloat(), text2r.bottom.toFloat())
        hcorn.set(text2r.left.toFloat(), (text2r.bottom - text2r.height() / 4).toFloat(), (text2r.left + text2r.height() / 4).toFloat(), text2r.bottom.toFloat())
        label_bg.arcTo(hcorn, 90f, 90f)
        label_bg.lineTo(text2r.left.toFloat(), (text1r.bottom + text1r.height() / 4).toFloat())
        hcorn.set((text2r.left - text2r.height() / 2).toFloat(), text1r.bottom.toFloat(), text2r.left.toFloat(), (text1r.bottom + text2r.height() / 2).toFloat())
        label_bg.arcTo(hcorn, 360f, -90f)

        label_bg.lineTo((text1r.left + text1r.height() / 8).toFloat(), text1r.bottom.toFloat())
        hcorn.set(text1r.left.toFloat(), (text1r.bottom - text1r.height() / 4).toFloat(), (text1r.left + text1r.height() / 4).toFloat(), text1r.bottom.toFloat())
        label_bg.arcTo(hcorn, 90f, 90f)
        label_bg.lineTo(text1r.left.toFloat(), (text1r.top + text1r.height() / 8).toFloat())
        hcorn.set(text1r.left.toFloat(), text1r.top.toFloat(), (text1r.left + text1r.height() / 4).toFloat(), (text1r.top + text1r.height() / 4).toFloat())
        label_bg.arcTo(hcorn, 180f, 90f)
        label_bg.close()

        mPaintBlack.alpha = 100
        canvas.drawPath(label_bg, mPaintBlack)
        mPaintBlack.alpha = 255

        canvas.drawText(t1, tx, ty, textColor)
        canvas.drawText(t2, tx, ty + textColor.textSize * 3 / 2, textColor)
    }

    internal fun printLabel(canvas: Canvas, t1: String, tx: Float, ty: Float, textColor: Paint) {
        textColor.getTextBounds(t1, 0, t1.length - 1, text1r)

        text1r.offset((tx - text1r.centerX()).toInt(), ty.toInt())
        text1r.inset((-textColor.textSize).toInt(), (-textColor.textSize).toInt() / 2)

        label_bg.reset()
        label_bg.moveTo(text1r.centerX().toFloat(), text1r.top.toFloat())
        label_bg.lineTo((text1r.right - text1r.height() / 8).toFloat(), text1r.top.toFloat())
        hcorn.set((text1r.right - text1r.height() / 4).toFloat(), text1r.top.toFloat(), text1r.right.toFloat(), (text1r.top + text1r.height() / 4).toFloat())
        label_bg.arcTo(hcorn, 270f, 90f)
        label_bg.lineTo(text1r.right.toFloat(), (text1r.bottom - text1r.height() / 8).toFloat())
        hcorn.set((text1r.right - text1r.height() / 4).toFloat(), (text1r.bottom - text1r.height() / 4).toFloat(), text1r.right.toFloat(), text1r.bottom.toFloat())
        label_bg.arcTo(hcorn, 0f, 90f)

        label_bg.lineTo((text1r.left + text1r.height() / 8).toFloat(), text1r.bottom.toFloat())
        hcorn.set(text1r.left.toFloat(), (text1r.bottom - text1r.height() / 4).toFloat(), (text1r.left + text1r.height() / 4).toFloat(), text1r.bottom.toFloat())
        label_bg.arcTo(hcorn, 90f, 90f)
        label_bg.lineTo(text1r.left.toFloat(), (text1r.top + text1r.height() / 8).toFloat())
        hcorn.set(text1r.left.toFloat(), text1r.top.toFloat(), (text1r.left + text1r.height() / 4).toFloat(), (text1r.top + text1r.height() / 4).toFloat())
        label_bg.arcTo(hcorn, 180f, 90f)
        label_bg.close()

        mPaintBlack.alpha = 100
        canvas.drawPath(label_bg, mPaintBlack)
        mPaintBlack.alpha = 255

        canvas.drawText(t1, tx, ty, textColor)
    }
}
