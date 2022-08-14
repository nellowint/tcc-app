package br.com.vieirateam.tcc.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.activity.MainActivity

object NotificationUtil {

    private var numMessages = 0
    private const val CHANNEL_ID = "tcc.vieirateam.com.br.CHANNEL_ID"
    private const val CHANNEL_NAME = "Notification"

    fun create(context: Context, intent: Intent, largeIcon: Bitmap,
               contentTitle: String, contentText: String) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT)

            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.description = context.getString(R.string.app_name)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(intent)

        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(
                context,
                CHANNEL_ID)
                .setNumber(++numMessages)
                .setContentIntent(pendingIntent)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setLargeIcon(largeIcon)
                .setVibrate(longArrayOf(1000, 1000))
                .setSmallIcon(R.drawable.ic_menu_notification)
                .setAutoCancel(true)

        val notification = builder.build()
        notificationManager.notify(notificationID, notification)
    }
}