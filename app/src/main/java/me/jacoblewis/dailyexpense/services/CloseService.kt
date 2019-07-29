package me.jacoblewis.dailyexpense.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import me.jacoblewis.dailyexpense.dependency.utils.MyApp


class CloseService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Toast.makeText(applicationContext, "APP Killed", Toast.LENGTH_SHORT).show()
        (application as MyApp).onDestroy()
        try {
            stopService(Intent(this, this.javaClass))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onTaskRemoved(rootIntent)
    }
}