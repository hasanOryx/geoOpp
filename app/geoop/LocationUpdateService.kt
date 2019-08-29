package com.oryx.geoop

import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.R
import android.graphics.BitmapFactory
import android.app.*
import android.content.Context
import android.graphics.Color
import android.os.Build


class LocationUpdateService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            startForegroundService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /* Used to build and start foreground service. */
    private fun startForegroundService(){

        // Create notification default intent.
        val intent = Intent()
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val channelId = "NOTIFICATION_CHANNEL_NAME"

        // Create notification builder.
        val builder = NotificationCompat.Builder(this,channelId)

        // Make notification show big text.
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle("GeoTask")
        bigTextStyle.bigText("GeoTask app is running")
        // Set big text style.
        builder.setStyle(bigTextStyle)

        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.drawable.ic_menu_mylocation)
        val largeIconBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_mylocation)
        builder.setLargeIcon(largeIconBitmap)
        // Make the notification max priority.
        //  builder.priority = Notification.PRIORITY_MAX
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true)

        /*    // Add Play button intent in notification.
            val playIntent = Intent(this, LocationUpdateService::class.java)
            playIntent.action = ACTION_PLAY.toString()
            val pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0)
            val playAction = NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent)
            builder.addAction(playAction)

            // Add Pause button intent in notification.
            val pauseIntent = Intent(this, LocationUpdateService::class.java)
            pauseIntent.action = ACTION_PAUSE.toString()
            val pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0)
            val prevAction = NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent)
            builder.addAction(prevAction)
            */
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "NOTIFICATION_CHANNEL_NAME",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lightColor = Color.DKGRAY
                setShowBadge(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }
            notificationManager.createNotificationChannel(channel)
        }
        // Build the notification.
        val notification = builder.build()

        // Start foreground service.
        startForeground(1, notification)

       // var intent2 = Intent(this, AppLocationService::class.java)
       // intent2.action = "oryx.track.UPDATE_LOCATION"
       // PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}