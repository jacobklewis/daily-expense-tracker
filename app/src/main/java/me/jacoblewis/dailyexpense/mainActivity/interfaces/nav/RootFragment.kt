package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

interface RootFragment : RootScreenElement {
    val transitionIn: Int
    val transitionOut: Int
    fun setRootElevation(el: Float)
}