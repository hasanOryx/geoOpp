package com.oryx.geoop

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootUpReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val i =  Intent(context, MainActivity::class.java)  // Intent(context, LocationUpdateService::class.java) //
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        // context.startService(i)
    }

}