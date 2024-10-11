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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.permissionandroid2.broadcast.MyBroadcastReceiver
import com.example.permissionandroid2.notification.MyNotification
import com.example.permissionandroid2.ui.theme.PermissionAndroid2Theme

class MainActivity : ComponentActivity() {

    private val myBroadcastReceiver = MyBroadcastReceiver()

    override fun onDestroy() {
        unregisterReceiver(myBroadcastReceiver)
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        registerReceiver(myBroadcastReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))

        setContent {
            var captureImageUri by remember { mutableStateOf<Uri?>(null) }

            val launchGallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {uri->
                captureImageUri = uri
            }
            //B5:
            val launchPermissionCamera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {

            }

            //B3:
            val launchPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it == true) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                }else{

                }
            }

            Column {
                Box(Modifier.fillMaxSize().clickable {
                    MyNotification.apply {
                        createChannel("LizAI")
                        createNotification()
                    }
                })
                //B4:
                Box(Modifier.size(300.dp).clickable {

                    launchGallery.launch("image/*")
                    requestPermissionNotification(
                        onGranted = {},
                        onExplainDenied = {},
                        onRequest = {
                            launchPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    )

                    /* requestPermissionCamera(
                         onGranted = {
                             //B6:
                             captureImageUri = CameraService.takeImageFromCameraDevice(this)
                             captureImageUri?.let { launchPermissionCamera.launch(it) }
                         }, onExplainDenied = {

                         }, onRequest = {
                             launchPermission.launch(Manifest.permission.CAMERA)
                         }
                     )*/
                }){
                    AsyncImage(modifier = Modifier.size(300.dp).align(Alignment.Center), contentDescription = "", model = captureImageUri)
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

