package com.bitla.ts.fcm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bitla.ts.utils.app_data.AppData

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.getStringExtra("URL") != null) {
//            val browserIntent = Intent(Intent.ACTION_VIEW,Uri.parse(intent.getStringExtra("URL")))
//            context.startActivity(browserIntent)
            AppData.androidUrl = intent.getStringExtra("URL")!!
            val uri = Uri.parse(AppData.androidUrl)
            val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
            try {
                context.startActivity(myAppLinkToMarket)
            } catch (e: Exception) {
                e.message
            }

        }
        val notificationId = intent.getIntExtra("notificationId", 0)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }
}
