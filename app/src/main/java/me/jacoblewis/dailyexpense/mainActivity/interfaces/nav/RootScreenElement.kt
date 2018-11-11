package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

interface RootScreenElement {
    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    fun navigateBack(): Boolean
}