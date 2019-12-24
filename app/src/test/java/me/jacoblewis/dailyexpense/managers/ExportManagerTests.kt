package me.jacoblewis.dailyexpense.managers

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.GlobalScope
import me.jacoblewis.dailyexpense.data.daos.PaymentsDao
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ExportManagerTests {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var context: Context
    @Mock
    lateinit var paymentsDao: PaymentsDao

    lateinit var exportManager: ExportManager

    @Before
    fun setup() {
        exportManager = ExportManager(context, paymentsDao, GlobalScope)
    }

    @Test
    fun testGatherData() {

    }
}