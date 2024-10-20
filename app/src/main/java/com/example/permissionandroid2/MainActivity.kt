package com.example.permissionandroid2

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import coil.compose.AsyncImage
import com.example.permissionandroid2.broadcast.MyBroadcastReceiver
import com.example.permissionandroid2.notification.MyNotification
import com.example.permissionandroid2.service.LongRunningWorker
import com.example.permissionandroid2.service.NotificationService
import com.example.permissionandroid2.service.NotificationWorker
import com.example.permissionandroid2.ui.theme.PermissionAndroid2Theme
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val myBroadcastReceiver = MyBroadcastReceiver()

    override fun onDestroy() {
        unregisterReceiver(myBroadcastReceiver)
        super.onDestroy()
    }

    private fun setOnTime() {
        val constrain = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val uploadReq = OneTimeWorkRequest.Builder(NotificationService::class.java)
            .setConstraints(constrain)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)//Expedited jobs only support network and storage constraints
            .build()
        val workMager = WorkManager.getInstance(CustomApplication.instance)
        workMager.enqueue(uploadReq)
    }
    private fun perioridc(){
        val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationService>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(CustomApplication.instance).enqueue(periodicWorkRequest)
    }

    private fun longRunning(){
        val longRunningWorkRequest = OneTimeWorkRequestBuilder<LongRunningWorker>().build()
        WorkManager.getInstance(CustomApplication.instance).enqueue(longRunningWorkRequest)
    }
    //B3 khoi tao service
    fun oneTimeService(){
        val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>().build()
        WorkManager.getInstance(CustomApplication.instance).enqueue(notificationWorker)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyNotification.createChannel("ddd")
        val uploadWorkRequest = OneTimeWorkRequestBuilder<NotificationService>().build()

        enableEdgeToEdge()
        registerReceiver(myBroadcastReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))

        setContent {
            var captureImageUri by remember { mutableStateOf<Uri?>(null) }
            val scope = rememberCoroutineScope()

            val launchGallery =
                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    captureImageUri = uri
                }
            //B5:
            val launchPermissionCamera =
                rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {

                }

            //B3:
            val launchPermission =
                rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                    if (it == true) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    } else {

                    }
                }

            Column {

                //B4:
                Box(
                    Modifier
                        .fillMaxSize()
                        .clickable {
                            perioridc()
                        }) {
                    AsyncImage(
                        modifier = Modifier
                            .size(300.dp)
                            .align(Alignment.Center),
                        contentDescription = "",
                        model = captureImageUri
                    )
                }
            }


        }
    }

    //B2: tao ham request permission
    private fun requestPermissionCamera(
        onGranted: () -> Unit,
        onExplainDenied: () -> Unit,
        onRequest: () -> Unit
    ) {
        val context = CustomApplication.instance
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                onGranted()
            }

            PackageManager.PERMISSION_DENIED -> {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,
                        Manifest.permission.CAMERA
                    )
                ) {
                    onExplainDenied()
                } else {
                    onRequest()
                }
            }
        }
    }

    private fun requestPermissionNotification(
        onGranted: () -> Unit,
        onExplainDenied: () -> Unit,
        onRequest: () -> Unit
    ) {
        val context = CustomApplication.instance
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
            PackageManager.PERMISSION_GRANTED -> {
                onGranted()
            }

            PackageManager.PERMISSION_DENIED -> {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                ) {
                    onExplainDenied()
                } else {
                    onRequest()
                }
            }
        }
    }
}

