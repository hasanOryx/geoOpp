package com.oryx.geoop

import android.widget.Toast
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context

class AppAlarm : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_SHORT).show()
    }
}