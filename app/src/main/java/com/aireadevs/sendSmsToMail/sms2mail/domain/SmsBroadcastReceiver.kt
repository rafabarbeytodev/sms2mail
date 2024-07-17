package com.aireadevs.sendSmsToMail.sms2mail.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import androidx.annotation.RequiresApi

/*****
 * Proyect: sms2mail
 * Package: com.aireadevs.sendSmsToMail.sms2mail.domain
 *
 * Created by Rafael Barbeyto Torrellas on 16/07/2024 at 22:20
 * More info: https://www.linkedin.com/in/rafa-barbeyto/
 *
 * All rights reserved 2024.
 *****/
class SmsBroadcastReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                val serviceIntent = Intent(context, SmsForegroundService::class.java)
                serviceIntent.action = Telephony.Sms.Intents.SMS_RECEIVED_ACTION
                serviceIntent.putExtras(intent.extras!!)
                context?.startForegroundService(serviceIntent)
            }
        }
    }

}