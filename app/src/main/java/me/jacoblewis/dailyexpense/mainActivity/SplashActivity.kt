package me.jacoblewis.dailyexpense.mainActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navToMain = Intent(this, MainActivity::class.java)
        startActivity(navToMain)
        finish()
    }
}