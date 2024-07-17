package com.aireadevs.sendSmsToMail.sms2mail.domain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.aireadevs.sendSmsToMail.sms2mail.R
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint

/*****
 * Proyect: sms2mail
 * Package: com.aireadevs.sendSmsToMail.sms2mail.domain
 *
 * Created by Rafael Barbeyto Torrellas on 16/07/2024 at 20:33
 * More info: https://www.linkedin.com/in/rafa-barbeyto/
 *
 * All rights reserved 2024.
 *****/
@AndroidEntryPoint
class SmsForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG,"Servicio iniciado...")
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            Notification.Builder(this)
                .setPriority(Notification.PRIORITY_DEFAULT)
        }
        return builder
            .setContentTitle("SMS Receiver Service")
            .setTicker("SMS Receiver Service")
            .setContentText("Listening for incoming SMS messages")
            .setSmallIcon(R.drawable.baseline_alternate_email_24)
            .setOngoing(true)  // Esto hace que la notificación sea no descartable
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "SMS Receiver Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "SmsReceiverServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}