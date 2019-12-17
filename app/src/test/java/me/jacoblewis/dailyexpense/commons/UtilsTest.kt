package me.jacoblewis.dailyexpense.commons

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UtilsTest {

    @Test
    fun testTranspose() {
        val matrix = listOf(
                listOf(1,2,3,4,5,6),
                listOf(7,8,9),
                listOf(10,11,12,13,14),
                listOf(15)
        )
        val trans = transposeStrict(matrix)

        trans.forEach {
            println(it.fold("") { acc, i -> "$acc|$i" })
        }
    }
}