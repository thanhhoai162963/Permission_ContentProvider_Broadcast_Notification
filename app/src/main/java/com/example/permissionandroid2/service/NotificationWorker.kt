package com.example.permissionandroid2.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.ServiceInfo
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.Build.VERSION_CODES.Q
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.permissionandroid2.R
import com.example.permissionandroid2.service.NotificationService.Companion.NOTIFICATION_CHANNEL
import com.example.permissionandroid2.service.NotificationService.Companion.NOTIFICATION_NAME


//B1: create worker service
class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        createForegroundInfo()
        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        return if (SDK_INT >= Q) {
            ForegroundInfo(15, sendNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(15, sendNotification())
        }
    }

    private fun sendNotification(): Notification {
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Android 14 service test")
            .setContentText("SDK14")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        if (SDK_INT >= O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(101, notification.build())
        return notification.build()
    }
}