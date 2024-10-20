package com.example.permissionandroid2.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.ServiceInfo
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.Build.VERSION_CODES.Q
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.permissionandroid2.R
import com.example.permissionandroid2.notification.MyNotification

class LongRunningWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        // Simulating a long-running task
        return try {
            // Your long-running code goes here
            performLongRunningTask()

            Result.success()  // Indicate successful completion
        } catch (e: Exception) {
            Result.failure()  // Indicate failure
        }
    }

    private suspend fun performLongRunningTask() {
        // Simulate a long task (e.g., sleep, network operation)
        // Replace with your actual long-running code
        for (i in 1..10) {
            // Perform part of the work
            Thread.sleep(1000) // Simulate work being done
            Log.d("bbb","123")
        }
    }
}