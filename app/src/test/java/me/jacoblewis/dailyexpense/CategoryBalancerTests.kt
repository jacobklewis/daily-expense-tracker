package me.jacoblewis.dailyexpense

import junit.framework.Assert.assertTrue
import me.jacoblewis.dailyexpense.commons.CategoryBalancer
import me.jacoblewis.dailyexpense.data.models.Category
import org.junit.Test

class CategoryBalancerTests {

    @Test
    fun testCategoryBalancer1() {
        val cats = listOf(Category("A",""), Category("B",""), Category("C",""))

        assertTrue(CategoryBalancer.balanceCategories(cats))

        println(cats)
    }

    @Test
    fun testCategoryBalancer2() {
        val cats = listOf(Category("A","", 0f), Category("B","", 0f), Category("C","", 0f), Category("D","", 0f))

        assertTrue(CategoryBalancer.balanceCategories(cats))

        println(cats)
    }

    @Test
    fun testCategoryBalancer3() {
        val cats = listOf(Category("A","", 0.5f), Category("B","", 0.5f), Category("C","", 1f), Category("D","", 1f))

        assertTrue(CategoryBalancer.balanceCategories(cats))
        assertTrue(!CategoryBalancer.balanceCategories(cats))

        println(cats)
    }

    @Test
    fun testCategoryBalancerPinned1() {
        val cats = listOf(Category("A","", 0.5f), Category("B","", 0.5f), Category("C","", 1f), Category("D","", 1f))

        assertTrue(CategoryBalancer.balanceCategories(cats, 0))
        assertTrue(!CategoryBalancer.balanceCategories(cats))

        println(cats)
    }
}