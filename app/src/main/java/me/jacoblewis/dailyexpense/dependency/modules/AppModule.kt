package me.jacoblewis.dailyexpense.dependency.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import me.jacoblewis.dailyexpense.commons.PREFS_SETTINGS
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

}