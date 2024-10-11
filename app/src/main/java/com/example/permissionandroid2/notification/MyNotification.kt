package com.example.permissionandroid2.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.permissionandroid2.CustomApplication
import com.example.permissionandroid2.R

object MyNotification {
    private const val CHANNEL_ID = "channel_reminder_1"
    private const val NOTIFICATION_ID = 115

    @RequiresApi(Build.VERSION_CODES.O)
    internal fun createChannel(channelName: String) {
        val activity = CustomApplication.instance
        val notificationChannel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH)
        val notificationManager: NotificationManager = activity.getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    internal fun createNotification() {
        val builder = NotificationCompat.Builder(CustomApplication.instance, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Message")
            .setContentText("You have a new message!")
            .setAutoCancel(true)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    CustomApplication.instance.resources,
                    R.drawable.ic_android_black_24dp
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(CustomApplication.instance)) {
            if (ActivityCompat.checkSelfPermission(
                    CustomApplication.instance,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}