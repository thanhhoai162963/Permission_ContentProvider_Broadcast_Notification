package com.example.permissionandroid2

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object CameraService {
    fun takeImageFromCameraDevice(context: Context): Uri {
        val photoFile = File(context.cacheDir, "camera_image.jpg")
        val uriImage = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            photoFile
        )
        return uriImage
    }
}

