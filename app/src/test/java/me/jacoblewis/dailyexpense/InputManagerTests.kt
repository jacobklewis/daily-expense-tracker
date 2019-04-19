package me.jacoblewis.dailyexpense

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.Assert.assertEquals
import me.jacoblewis.dailyexpense.managers.EnterInputManager
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class InputManagerTests {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun testAddRemove() {
        val inputMan = EnterInputManager()

        assertEquals(inputMan.currentValue.value, 0.0)

        inputMan.addDigit(2)
        assertEquals(inputMan.currentValue.value, 0.02)

        inputMan.addDigit(0)
        assertEquals(inputMan.currentValue.value, 0.20)

        inputMan.removeDigit()
        assertEquals(inputMan.currentValue.value, 0.02)
    }
}