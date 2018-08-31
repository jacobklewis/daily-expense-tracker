package me.jacoblewis.dailyexpense.commons

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun openURI(activity: Activity, uriPriority: List<String>) {
    val intent = Intent(Intent.ACTION_VIEW)
    uriPriority.firstOrNull { uriString ->
        intent.data = Uri.parse(uriString)
        myStartActivity(activity, intent)
    } ?: run {
        //Well if this also fails, we have run out of options, inform the user.
        Toast.makeText(activity, "Unable to display content. Please try again.", Toast.LENGTH_SHORT).show()
    }
}

private fun myStartActivity(activity: Activity, aIntent: Intent): Boolean {
    return try {
        activity.startActivity(aIntent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    }

}