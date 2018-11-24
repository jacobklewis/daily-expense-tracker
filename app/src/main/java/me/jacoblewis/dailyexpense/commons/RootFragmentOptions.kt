package me.jacoblewis.dailyexpense.commons

import me.jacoblewis.dailyexpense.R

data class RootFragmentOptions(
        val screenClass: Class<*>,
        val drawerNavId: Int = 0,
        val transitionIn: Int = R.anim.nothing,
        val transitionOut: Int = R.anim.nothing
) {
    val screenTag: String = screenClass.name
}