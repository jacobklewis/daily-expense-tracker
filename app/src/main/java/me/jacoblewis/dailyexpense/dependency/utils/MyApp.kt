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
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.network.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.adapters.GeneralItemAdapter
import me.jacoblewis.dailyexpense.commons.MORNING_CHANNEL_ID
import me.jacoblewis.dailyexpense.commons.PREFS_SETTINGS
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.managers.BalanceManager
import me.jacoblewis.dailyexpense.managers.ExportManager
import me.jacoblewis.dailyexpense.managers.SyncManager
import me.jacoblewis.dailyexpense.services.CloseService
import me.jacoblewis.dailyexpense.viewModels.CategoryViewModel
import me.jacoblewis.dailyexpense.viewModels.EnterPaymentViewModel
import me.jacoblewis.dailyexpense.viewModels.PaymentViewModel
import me.jacoblewis.dailyexpense.workers.MorningBalanceWorker
import me.jacoblewis.jklcore.Tools
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

/**
 * Created by Jacob on 11/28/2017.
 */
class MyApp : Application(), LifecycleObserver, CoroutineScope {


    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate() {
        super.onCreate()
        job = Job()
        appScope = this
        startKoin {
            androidContext(applicationContext)
            modules(myModule)
        }
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
        var appScope: CoroutineScope? = null
        val myModule = module {
            // ViewModels
            viewModel { PaymentViewModel(get(), get()) }
            viewModel { CategoryViewModel(get(), get(), get()) }
            viewModel { EnterPaymentViewModel() }
            // Main App
            single { Tools.getInstance(get()) }
            single { androidContext().getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE) }
            single { androidContext() as CoroutineScope }
            // DB
            single { BalancesDB.getInstance(get()) }
            single { get<BalancesDB>().paymentsDao() }
            single { get<BalancesDB>().categoriesDao() }
            single { get<BalancesDB>().budgetsDao() }
            // Others
            factory { BalanceManager(get(), get()) }
            factory { GeneralItemAdapter(get(), get(), get()) }
            factory { ExportManager(get(), get(), get()) }
            factory { SyncManager(get(), get()) }
        }
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