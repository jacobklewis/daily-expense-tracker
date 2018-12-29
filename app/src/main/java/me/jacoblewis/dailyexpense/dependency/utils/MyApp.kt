package me.jacoblewis.dailyexpense.dependency.utils

import android.app.Application
import me.jacoblewis.dailyexpense.dependency.AppComponent
import me.jacoblewis.dailyexpense.dependency.DaggerAppComponent
import me.jacoblewis.dailyexpense.dependency.modules.AppModule
import me.jacoblewis.dailyexpense.dependency.modules.DBModule

/**
 * Created by Jacob on 11/28/2017.
 */
class MyApp : Application() {

    private val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .dBModule(DBModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        graph = appComponent
        graph.inject(this)
    }

    companion object {
        lateinit var graph: AppComponent
    }
}