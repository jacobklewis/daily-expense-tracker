package me.jacoblewis.dailyexpense.dependency.utils

import android.app.Application
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import me.jacoblewis.dailyexpense.dependency.AppComponent
import me.jacoblewis.dailyexpense.dependency.DaggerAppComponent
import me.jacoblewis.dailyexpense.dependency.modules.AppModule
import me.jacoblewis.dailyexpense.dependency.modules.DBModule
import me.jacoblewis.dailyexpense.services.CloseService
import kotlin.coroutines.CoroutineContext

/**
 * Created by Jacob on 11/28/2017.
 */
class MyApp : Application(), LifecycleObserver, CoroutineScope {

    private val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .dBModule(DBModule(this))
                .build()
    }

    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate() {
        super.onCreate()
        job = Job()
        graph = appComponent
        graph.inject(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        startService(Intent(this, CloseService::class.java))
    }

    companion object {
        lateinit var graph: AppComponent
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        job.cancelChildren()
        job.cancel()
    }
}