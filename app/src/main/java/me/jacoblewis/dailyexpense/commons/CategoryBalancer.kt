package me.jacoblewis.dailyexpense.commons

import me.jacoblewis.dailyexpense.data.models.Category

object CategoryBalancer {
    private val BIAS = 0.001f

    /**
     * Return true iff categories were rebalanced
     */
    fun balanceCategories(categories: List<Category>, pinned: Int = -1): Boolean {
        if (!isBalanced(categories)) {
            balanceNow(pinned, categories)
            return true
        }
        return false
    }

    private fun balanceNow(pinned: Int, categories: List<Category>) {
        val pinnedOffset = 1f - (if (pinned == -1) 0f else categories[pinned].budget)
        assert(pinnedOffset in 0.0..1.0, lazyMessage = { "Invalid Pinned Category" })
        val sum = categories.mapIndexed { i, cat ->
            if (i == pinned) 0f else cat.budget
        }.sum()
        if (sum != 0f) {
            categories.forEachIndexed { i, cat ->
                if (i != pinned) {
                    cat.budget *= pinnedOffset / sum
                }
            }
        } else {
            categories.forEachIndexed { i, cat ->
                if (i != pinned) {
                    cat.budget = pinnedOffset / categories.size
                }
            }
        }
    }

    private fun isBalanced(categories: List<Category>): Boolean {
        return Math.abs(categories.sumByDouble { it.budget.toDouble() } - 1f) < BIAS
    }


}