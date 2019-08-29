package com.oryx.geoop

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.os.SystemClock.sleep
import android.widget.Toast
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.delay
import org.json.JSONObject


class AppLocationService  : BroadcastReceiver() {

    val ACTION_PROCESS_UPDATE = "oryx.track.UPDATE_LOCATION"
    override fun onReceive(context: Context, p1: Intent?) {
        // This intent is used to start background service. The same service will be invoked for each invoke in the loop.
        if (p1 != null) {
            val action = p1.action
            if (ACTION_PROCESS_UPDATE == action){
                var result = LocationResult.extractResult(p1)
                if (result != null){
                    alarmMgr?.cancel(alarmIntent)

                    location = result.lastLocation

                    val freeOp = when (assignmentsOpenedList.filter
                                { it.status == "On the way" || it.status == "Started" }.size ) {
                        0 -> true
                        else -> false
                    }

                    val jsonStr =
                        "{\"unique_id\": $uniqueID, \"lat\": ${location.latitude}, \"lon\": ${location.longitude}, " +
                                "\"speed\": ${location.speed}, \"free_op\": $freeOp}"
                    val jObj = JSONObject(jsonStr)
                  //  Toast.makeText(p0, "latitude = ${location.latitude}",Toast.LENGTH_LONG).show()
                    if (mWebSocketClient.isOpen) {
                        mWebSocketClient.send(jObj.toString())
                    } else {
                        try {
                            client.connectWebSocket(context)
                            sleep(500)
                            mWebSocketClient.send(jObj.toString())
                        } catch (e : Exception){
                            Toast.makeText(context, "not connected $e", Toast.LENGTH_LONG).show()
                        }
                    }
                    alarmMgr?.setExactAndAllowWhileIdle(
                        AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime()+ AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent)
                }
            }
        }
    }
}