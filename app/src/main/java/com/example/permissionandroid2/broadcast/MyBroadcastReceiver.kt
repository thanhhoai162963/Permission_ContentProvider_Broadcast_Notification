package com.example.permissionandroid2.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


//B1: tao class broadcast
class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context,intent?.action.toString(),Toast.LENGTH_LONG).show()
    }
}