package me.jacoblewis.dailyexpense.mainActivity.interfaces.nav

interface RootScreenElement {
    /**
     * Tag used for fragment state management
     */
    val screenTag: String
    /**
     * Navigate Back
     * @return true iff the screen performed navigation
     */
    fun navigateBack(): Boolean
}