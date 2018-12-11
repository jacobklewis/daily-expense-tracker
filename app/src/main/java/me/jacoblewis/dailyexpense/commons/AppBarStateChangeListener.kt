package me.jacoblewis.dailyexpense.commons

import com.google.android.material.appbar.AppBarLayout


fun AppBarLayout.addStateChangeListener(onStateChanged: (appBarLayout: AppBarLayout, state: State) -> Unit) {
    addOnOffsetChangedListener(AppBarStateChangeListener(onStateChanged))
}

enum class State {
    EXPANDED,
    COLLAPSED,
    IDLE
}

data class AppBarStateChangeListener(val onStateChanged: (appBarLayout: AppBarLayout, state: State) -> Unit) : AppBarLayout.OnOffsetChangedListener {

    private var mCurrentState = State.IDLE

    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        when {
            i == 0 -> {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED)
                }
                mCurrentState = State.EXPANDED
            }
            Math.abs(i) >= appBarLayout.totalScrollRange -> {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED)
                }
                mCurrentState = State.COLLAPSED
            }
            else -> {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE)
                }
                mCurrentState = State.IDLE
            }
        }
    }
}