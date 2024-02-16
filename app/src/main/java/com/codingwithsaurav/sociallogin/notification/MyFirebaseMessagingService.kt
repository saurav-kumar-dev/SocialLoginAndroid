package com.codingwithsaurav.sociallogin.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService() : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("khbbfkdsjf", "Firebase Token $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("khbbfkdsjf", "Firebase RemoteMessage $message")
        showNotification(message)
    }

    private fun showNotification(message: RemoteMessage) {
        message.let {
            val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            // checking if android version is greater than oreo(API 26) or not
            val notificationBuilder: Notification.Builder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationChannel = NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.GREEN
                    notificationChannel.enableVibration(false)
                    notificationChannel.description = CHANNEL_DESCRIPTION
                    notificationManager.createNotificationChannel(notificationChannel)
                    Notification.Builder(this, CHANNEL_ID)
                } else {
                    Notification.Builder(this)
                }

            notificationBuilder
                .setContentTitle(message.notification?.title)
                .setSubText(message.notification?.body)
                .setSmallIcon(R.drawable.ic_notifications_icon)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_notifications_icon))
                .setContentIntent(getPendingIntent())

            notificationManager.notify(Random.nextInt(Int.MAX_VALUE), notificationBuilder.build())
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(
            this,
            Random.nextInt(Int.MAX_VALUE),
            intent,
            getPendingIntentFlag()
        )
    }

    private fun getPendingIntentFlag(isMutuable: Boolean = true): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return if (isMutuable) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_IMMUTABLE
        }
        return PendingIntent.FLAG_UPDATE_CURRENT
    }

    companion object {
        const val CHANNEL_NAME = "Push Notification"
        const val CHANNEL_DESCRIPTION = "Channel for push Notification"
        const val CHANNEL_ID = "push_notification"
    }
}
