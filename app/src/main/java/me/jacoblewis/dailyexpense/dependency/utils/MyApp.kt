package me.jacoblewis.dailyexpense.dependency.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.MORNING_CHANNEL_ID
import me.jacoblewis.dailyexpense.dependency.AppComponent
import me.jacoblewis.dailyexpense.dependency.DaggerAppComponent
import me.jacoblewis.dailyexpense.dependency.modules.AppModule
import me.jacoblewis.dailyexpense.dependency.modules.DBModule
import me.jacoblewis.dailyexpense.services.CloseService
import me.jacoblewis.dailyexpense.workers.MorningBalanceWorker
import java.lang.Exception
import java.util.concurrent.TimeUnit
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
        appScope = this
        graph = appComponent
        graph.inject(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        try {
            startService(Intent(this, CloseService::class.java))
        } catch (ignore: Exception) {
            /* ignore */
        }
        createNotificationChannel()
        createMorningBalanceWorker()
    }

    companion object {
        lateinit var graph: AppComponent
        var appScope: CoroutineScope? = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        appScope = null
        job.cancelChildren()
        job.cancel()
    }

    // Morning Notification Channels
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(MORNING_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createMorningBalanceWorker() {
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(true)
                .build()
        val morningBalanceRequest = PeriodicWorkRequestBuilder<MorningBalanceWorker>(12, TimeUnit.HOURS, 6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance().cancelAllWork()
        WorkManager.getInstance().enqueue(morningBalanceRequest)
        Log.i("Worker", "Worker Setup")
    }
}