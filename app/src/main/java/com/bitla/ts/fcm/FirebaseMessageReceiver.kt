package com.bitla.ts.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.MediaPlayer
import android.os.Build
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.presentation.view.activity.DomainActivity
import com.bitla.ts.presentation.view.activity.SplashScreen
import com.bitla.ts.presentation.view.activity.notifications.NotificationDetailsPhase3Actvity
import com.bitla.ts.utils.constants.NOTIFICATION_DEFAULT_SOUND
import com.bitla.ts.utils.constants.NOTIFICATION_SILENT
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import toast
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class FirebaseMessageReceiver : FirebaseMessagingService(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var message: String? = null
    private var tempMessage: String? = null
    private var notificationId: String? = null
    private var GROUP_ID = "com.bitla.ticketsimply"
    var notiId: Int = 1


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        tts = TextToSpeech(this, this)
        notificationId = remoteMessage.data["notification_id"]
        var title = ""
        var body = ""
        var imageURL = ""

        if (!remoteMessage.data["title"].equals("") || !remoteMessage.data["title"]
                .isNullOrEmpty()
        ) {
            title = remoteMessage.data["title"].toString()
        }


        val intent = Intent("FCM_NOTIFICATION_RECEIVED")
        // Optionally, you can put extra data in the intent
        intent.putExtra("refresh",true)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)


        if (!remoteMessage.data["body"].equals("") || !remoteMessage.data["message"]
                .isNullOrEmpty()
        ) {
            body = remoteMessage.data["body"].toString()
            message = remoteMessage.data["body"].toString()
            tempMessage = remoteMessage.data["body"].toString()

        }

        if (!remoteMessage.data["image_url"].equals("") || !remoteMessage.data["image_url"]
                .isNullOrEmpty()
        ) {
            imageURL = remoteMessage.data["image_url"].toString()
        }

        if (remoteMessage.data["title"] != null && remoteMessage.data["body"] != null && remoteMessage.data["title"] != "get_log") {
            showNotification(
                remoteMessage.data["title"],
                remoteMessage.data["body"],
                remoteMessage.data["image"]
            )

        } else if (remoteMessage.notification != null) {
            showNotification(
                remoteMessage.notification?.title,
                remoteMessage.notification?.body,
                remoteMessage.notification?.imageUrl.toString()
            )
        }
    }

    // Method to get the custom Design for the display of
    // notification.
    private fun getCustomDesign(
        title: String?, message: String?
    ): RemoteViews {
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification_layout)
        remoteViews.setTextViewText(R.id.header_title, title)
        remoteViews.setTextViewText(R.id.message, message)
        return remoteViews
    }

    private fun showNotification(title: String?, msg: String?, imageUrl: String?) {
        // Pass the intent to switch to the MainActivity
        var message = msg

        if (message != null && message?.contains("voice:", true)!!) {

            message = message!!.replace("VOICE:", "")
        }

        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification_layout)

        if (title.isNullOrEmpty() || title == "") {
            remoteViews.setViewVisibility(R.id.header_title, View.GONE)
        }
        if (message.isNullOrEmpty() || message == "") {
            remoteViews.setViewVisibility(R.id.message, View.GONE)
        }

        if (!imageUrl.isNullOrEmpty() || imageUrl != "") {
            val bitmap: Bitmap?
            //bitmap = getBitmapfromUrl(imageUrl.toString())
            try {
                val url = URL(imageUrl)
                val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                bitmap = image
                setBitmap(remoteViews, R.id.notificationImage, bitmap)
                remoteViews.setImageViewBitmap(R.id.notificationImage, bitmap)

            } catch (e: Exception) {
                remoteViews.setViewVisibility(androidx.core.R.id.notification_background, View.GONE)
            }
        } else {
            remoteViews.setViewVisibility(androidx.core.R.id.notification_background, View.GONE)
        }

        remoteViews.setTextViewText(R.id.header_title, title)
        remoteViews.setTextViewText(R.id.message, message)

        // Assign channel ID
        val channelId = "notification_channel"
      //  val notiId: Int? = notificationId?.toInt()
        //val notiId = (0..100).random()

        val intent = if (PreferenceUtils.getLogin().api_key.isNotEmpty()) {
            val privilegeBase = BaseActivity.PrivilegeManager.getPrivilegeBase(applicationContext)

            if (privilegeBase?.isAgentLogin == true) {
                Intent(applicationContext, SplashScreen::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            } else {
                Intent(applicationContext, NotificationDetailsPhase3Actvity::class.java).apply {
                    putExtra(getString(R.string.notification_id), notificationId)
                    putExtra(getString(R.string.notification_title), title)
                    putExtra(getString(R.string.notification_message), message)
                    putExtra(getString(R.string.notification_image), imageUrl)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            }

        } else {
            Intent(applicationContext, DomainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }
        val uniqueInt = (System.currentTimeMillis() and 0xff).toInt()
        val openActivityPendingIntent =
            PendingIntent.getActivity(this, uniqueInt, intent, FLAG_MUTABLE)

        val buttonIntent = Intent(applicationContext, ButtonReceiver::class.java).apply {
            putExtra("notificationId", channelId)
        }

        val pendingIntent1 =
            PendingIntent.getBroadcast(applicationContext, 0, buttonIntent, FLAG_MUTABLE)


        val builder = NotificationCompat.Builder(applicationContext, channelId)
           // .setCustomBigContentView(remoteViews).setLargeIcon(getBitmapfromUrl(imageUrl))
            .setShowWhen(true)
            .setAutoCancel(true)
            .addAction(R.mipmap.ic_launcher, getString(R.string.close), pendingIntent1)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000)).setOnlyAlertOnce(true)
            .setContentTitle(title).setContentText(message)
            .setContentIntent(openActivityPendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setGroup(GROUP_ID)
            .setLargeIcon(getBitmapfromUrl(imageUrl))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(getBitmapfromUrl(imageUrl)))

        val summaryBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setCustomBigContentView(remoteViews).setLargeIcon(getBitmapfromUrl(imageUrl))
            .setShowWhen(true)
            .addAction(R.mipmap.ic_launcher, getString(R.string.close), pendingIntent1)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000)).setOnlyAlertOnce(true)
            .setContentTitle(title).setContentText(message)
            .setContentIntent(openActivityPendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setGroup(GROUP_ID)
            .setGroupSummary(true)
            .setStyle(NotificationCompat.InboxStyle()
                .setSummaryText("You have new messages"))
            .setLargeIcon(getBitmapfromUrl(imageUrl))
            .setPriority(NotificationCompat.PRIORITY_MAX)
           // .setStyle(NotificationCompat.BigPictureStyle().bigPicture(getBitmapfromUrl(imageUrl)))


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (notificationManager.areNotificationsEnabled()) {
                if (PreferenceUtils.getNotificationSoundType(applicationContext) == NOTIFICATION_DEFAULT_SOUND) {
                    builder.setSilent(true)
                    val mp: MediaPlayer = MediaPlayer.create(applicationContext, R.raw.air_bus_horn)
                    mp.start()
                } else if (PreferenceUtils.getNotificationSoundType(applicationContext) == NOTIFICATION_SILENT) {
                    builder.setSilent(true)
                }
            }
        }

        // Check if the Android Version is greater than Oreo

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, "web_app", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }


            if (notiId != null) {
              /*  notificationManager.notify(notiId, builder.build())
                notificationManager.notify(0,summaryBuilder.build())*/
                try {
                    NotificationManagerCompat.from(this).apply {
                        if (ActivityCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {

                            return
                        }
                        builder.mActions.clear()
                        notify(++notiId, builder.build())
                        notify(0, summaryBuilder.build())
                    }
                }catch (e :Exception){
                    if(BuildConfig.DEBUG){
                        e.printStackTrace()
                    }
                }
            }
    }

    private fun setBitmap(views: RemoteViews, resId: Int, bitmap: Bitmap) {
        val proxy = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(proxy)
        c.drawBitmap(bitmap, Matrix(), null)
        views.setImageViewBitmap(resId, proxy)
    }

    /*fun getBitmapfromUrl(imageUrl: String?): Bitmap? {

        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Timber.e("Error in getting notification image: " + e.localizedMessage)
            null
        }
    }*/

    private fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        if (!imageUrl.isNullOrEmpty()) {
            val imageExists = doesImageExist(imageUrl)
            if (imageExists) {
                return try {
                    val url = URL(imageUrl)
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()

                    // Check the content type of the URL
                    val contentType = connection.contentType
                    if (contentType?.startsWith("image/") == true) {
                        val input: InputStream = connection.inputStream
                        BitmapFactory.decodeStream(input)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    Timber.e("Error in getting notification image: ${e.localizedMessage}")
                    null
                }
            }
        }
        return null
    }


    private fun doesImageExist(imageUrl: String): Boolean {
        return try {
            val url = URL(imageUrl)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "HEAD"

            // Set a reasonable timeout in milliseconds
            httpURLConnection.connectTimeout = 5000
            httpURLConnection.readTimeout = 5000

            val responseCode = httpURLConnection.responseCode
            responseCode == HttpURLConnection.HTTP_OK
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }


    override fun onInit(status: Int) {
        try {
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Timber.d("TTS", "The Language not supported!")
                } else {
                    if (tempMessage != null && tempMessage?.contains("voice:", true) == true) {
                        tempMessage =
                            tempMessage?.replace("VOICE:".lowercase(Locale.getDefault()), "")
                        PreferenceUtils.putString("notificationReceived", "true")
                        speakOut()
                    }
                }
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }

    private fun speakOut() {
        try {
            if (tts != null) {
                if (message != null && message?.contains("voice:", true) == true) {
                    message = message?.replace("VOICE:", "")
                }
                tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "")
            }
        } catch (e: Exception) {
            toast(getString(R.string.something_went_wrong))
        }

    }

    override fun onDestroy() {
        /* if (tts != null) {
             tts!!.stop()
             tts!!.shutdown()
         }*/
        super.onDestroy()
    }
}

class ButtonReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val notificationID = p1?.getIntExtra("notificationId", 0)
        val manager1: NotificationManagerCompat = NotificationManagerCompat.from(p0!!)
        if (notificationID != null) {
            manager1.cancel(notificationID)
        }
    }
}