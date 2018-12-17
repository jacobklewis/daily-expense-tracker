package me.jacoblewis.dailyexpense.commons

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.Toast
import butterknife.ButterKnife
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.R
import okhttp3.Dispatcher
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


fun openURI(activity: Activity, uriPriority: List<String>) {
    val intent = Intent(Intent.ACTION_VIEW)
    uriPriority.firstOrNull { uriString ->
        intent.data = Uri.parse(uriString)
        myStartActivity(activity, intent)
    } ?: run {
        //Well if this also fails, we have run out of options, inform the user.
        Toast.makeText(activity, "Unable to display content. Please try again.", Toast.LENGTH_SHORT).show()
    }
}

private fun myStartActivity(activity: Activity, aIntent: Intent): Boolean {
    return try {
        activity.startActivity(aIntent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    }

}


object AnimationUtils {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun registerCircularRevealAnimation(context: Context, view: View, revealSettings: RevealAnimationSetting, startColor: Int, endColor: Int) {
        view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                v.removeOnLayoutChangeListener(this)
                val cx = revealSettings.centerX
                val cy = revealSettings.centerY
                val width = revealSettings.width
                val height = revealSettings.height
                val duration = context.resources.getInteger(R.integer.time_animate_medium)

                //Simply use the diagonal of the view
                val finalRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, finalRadius).setDuration(duration.toLong())
                anim.interpolator = FastOutSlowInInterpolator()
                anim.start()
                startColorAnimation(view, startColor, endColor, duration)
            }
        })
    }

    fun startCircularExitAnimation(context: Context, view: View, revealSettings: RevealAnimationSetting, startColor: Int, endColor: Int) {
        val cx = revealSettings.centerX
        val cy = revealSettings.centerY
        val width = revealSettings.width
        val height = revealSettings.height
        val duration = context.resources.getInteger(R.integer.time_animate_medium)

        val initRadius = Math.sqrt(width * width.toDouble() + height * height).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initRadius, 0f)
        anim.duration = duration.toLong() + 100L
        anim.interpolator = FastOutSlowInInterpolator()
        anim.start()
        startColorAnimation(view, startColor, endColor, duration)
    }

    internal fun startColorAnimation(view: View, startColor: Int, endColor: Int, duration: Int) {
        val anim = ValueAnimator()
        anim.setIntValues(startColor, endColor)
        anim.setEvaluator(ArgbEvaluator())
        anim.addUpdateListener { valueAnimator -> view.setBackgroundColor(valueAnimator.animatedValue as Int) }
        anim.duration = duration.toLong()
        anim.start()
    }
}

@Parcelize
data class RevealAnimationSetting(
        val centerX: Int,
        val centerY: Int,
        val width: Int,
        val height: Int
) : Parcelable

infix fun View.revealSettingsTo(view: View) = RevealAnimationSetting(
        centerX = (x + width / 2).toInt(),
        centerY = (y + height / 2).toInt(),
        width = view.measuredWidth,
        height = view.measuredHeight
)

inline fun androidx.fragment.app.Fragment.oCV(@LayoutRes layoutId: Int, container: ViewGroup?, config: (View) -> Unit): View? {
    val rootView = layoutInflater.inflate(layoutId, container, false)
    ButterKnife.bind(this, rootView)
    config(rootView)
    return rootView
}

val Float.asCurrency
    get() = NumberFormat.getCurrencyInstance().format(this)

val String.fromCurrency
    get() = NumberFormat.getCurrencyInstance().parse(this).toFloat()

infix fun Date.formatAs(pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun wait(millis: Long, block:()->Unit) = GlobalScope.launch(Dispatchers.Main) {
    delay(millis)
    block()
}