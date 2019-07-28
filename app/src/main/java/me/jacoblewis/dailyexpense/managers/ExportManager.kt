package me.jacoblewis.dailyexpense.managers

import android.content.Context
import androidx.annotation.UiThread
import me.jacoblewis.dailyexpense.data.BalancesDB
import javax.inject.Inject

class ExportManager
@Inject constructor(val context: Context, val db: BalancesDB) {

    @UiThread
    fun exportNow() {
        
    }
}