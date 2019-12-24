package me.jacoblewis.dailyexpense.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import me.jacoblewis.dailyexpense.R
import me.jacoblewis.dailyexpense.commons.MORNING_CHANNEL_ID
import me.jacoblewis.dailyexpense.commons.asCurrency
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.mainActivity.MainActivity
import me.jacoblewis.dailyexpense.managers.BalanceManager
import javax.inject.Inject

class MorningBalanceWorker(private val appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    @Inject
    lateinit var balanceManager: BalanceManager

    override fun doWork(): Result {
        Log.i("Worker", "Worker Started")

        MyApp.graph.inject(this)
        // Fetch Current Balance
        val currentDailyBalance = balanceManager.fetchDailyBalanceNow()

        // Create an explicit intent for an Activity in your app
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(appContext, 0, intent, 0)

        val builder = NotificationCompat.Builder(appContext, MORNING_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_cash)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(NotificationCompat.BigTextStyle()
                        .setSummaryText("You're Doing Well!")
                        .setBigContentTitle("Today's Allowance: ${currentDailyBalance.asCurrency}"))
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(appContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(0, builder.build())
        }
        Log.i("Worker", "Worker Finished!")
        return Result.success()
    }
}