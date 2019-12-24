package me.jacoblewis.dailyexpense.dependency.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineScope
import me.jacoblewis.dailyexpense.commons.PREFS_SETTINGS
import me.jacoblewis.dailyexpense.data.daos.BudgetsDao
import me.jacoblewis.dailyexpense.data.daos.PaymentsDao
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.managers.BalanceManager
import me.jacoblewis.jklcore.Tools
import javax.inject.Singleton

/**
 * Created by Jacob on 11/28/2017.
 */
@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    internal fun providesContext(): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    internal fun providesTools(): Tools {
        return Tools.getInstance(providesContext())
    }

    @Provides
    @Singleton
    internal fun providesSettingsPrefs(): SharedPreferences {
        return application.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)!!
    }

    @Provides
    @Reusable
    internal fun provides(paymentsDao: PaymentsDao, budgetsDao: BudgetsDao): BalanceManager {
        return BalanceManager(paymentsDao, budgetsDao)
    }

    @Provides
    @Singleton
    internal fun providesGlobalCoroutineScope(): CoroutineScope {
        return application as MyApp
    }

}