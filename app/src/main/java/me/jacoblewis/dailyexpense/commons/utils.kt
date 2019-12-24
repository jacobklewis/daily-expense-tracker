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
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import butterknife.ButterKnife
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
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

val Number.asCurrency: String
    get() = NumberFormat.getCurrencyInstance().format(this)

val String.fromCurrency: Float
    get() {
        val num = try {
            NumberFormat.getCurrencyInstance().parse(this).toFloat()
        } catch (e: Exception) {
            try {
                this.toFloat()
            } catch (e: Exception) {
                0f
            }
        }
        return num
    }

infix fun Date.formatAs(pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

// Optional Conditional Chaining

/**
 * Continue to return non-null object as long as block is true
 */
inline fun <T> T.and(block: (T) -> Boolean): T? {
    return if (block(this)) this else null
}

inline fun <R, A> ifAll(a: A?, block: (A) -> R?): R? {
    return if (a != null) {
        block(a)
    } else null
}

inline fun <R, A, B> ifAll(a: A?, b: B?, block: (A, B) -> R?): R? {
    return if (a != null && b != null) {
        block(a, b)
    } else null
}

inline fun <R, A, B, C> ifAll(a: A?, b: B?, c: C?, block: (A, B, C) -> R?): R? {
    return if (a != null && b != null && c != null) {
        block(a, b, c)
    } else null
}

inline fun <R, A, B, C, D> ifAll(a: A?, b: B?, c: C?, d: D?, block: (A, B, C, D) -> R?): R? {
    return if (a != null && b != null && c != null && d != null) {
        block(a, b, c, d)
    } else null
}

inline fun <R, A, B, C, D, E> ifAll(a: A?, b: B?, c: C?, d: D?, e: E?, block: (A, B, C, D, E) -> R?): R? {
    return if (a != null && b != null && c != null && d != null && e != null) {
        block(a, b, c, d, e)
    } else null
}

fun wait(millis: Long, block: () -> Unit) = MyApp.appScope?.launch(Dispatchers.Main) {
    delay(millis)
    block()
}

fun <T, J> observeBoth(a: LiveData<T>, b: LiveData<J>, owner: LifecycleOwner, observer: (T, J) -> Unit) {
    var cResA: T? = null
    var cResB: J? = null
    fun postResult() {
        if (cResA != null && cResB != null) {
            observer(cResA!!, cResB!!)
        }
    }
    a.observe(owner, androidx.lifecycle.Observer { resA ->
        cResA = resA
        postResult()
    })
    b.observe(owner, androidx.lifecycle.Observer { resB ->
        cResB = resB
        postResult()
    })
}

inline fun <reified T> Any?.asNum(): T? {
    val num = (this as? Number)
    return when(T::class) {
        Float::class -> num?.toFloat()
        Double::class -> num?.toDouble()
        Int::class -> num?.toInt()
        Long::class -> num?.toLong()
        else -> num
    } as? T
}

/**
 * Modified from:
 * https://gist.github.com/clementgarbay/49288c006252955c2a3c6139a61ca92a
 */
fun <E> transposeStrict(xs: List<List<E?>>): List<List<E?>> {
    fun <E> List<E?>.head(): E? = this.firstOrNull()
    fun <E> List<E?>.tail(): List<E?> = if (isEmpty()) this else this.takeLast(this.size - 1)
    fun <E> E.append(xs: List<E>): List<E> = listOf(this).plus(xs)

    return if (!xs.all { it.isEmpty() }) {
        xs.map { it.head() }.append(transposeStrict(xs.map { it.tail() }))
    } else {
        listOf()
    }
}