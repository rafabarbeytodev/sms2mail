package com.aireadevs.sendSmsToMail.sms2mail.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms
import android.util.Log
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.AUTH
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.FROM_ADDRESS
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.HOST
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.PASSWORD
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.PORT
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.SUBJECT
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.TAG
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

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
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if (intent.action == Sms.Intents.SMS_RECEIVED_ACTION) {
                Log.i(TAG, "SMS recibido...")
                val serviceIntent = Intent(context, SmsForegroundService::class.java)
                serviceIntent.action = Sms.Intents.SMS_RECEIVED_ACTION
                serviceIntent.putExtras(intent.extras!!)
                context?.startForegroundService(serviceIntent)

                val sms = Sms.Intents.getMessagesFromIntent(intent)
                val extras = intent.extras
                val simSlotReceptionIndex = extras?.getInt("subscription", -1)
                var message = ""
                val origin = sms.first().originatingAddress ?: ""
                val dateIn = sms.first().timestampMillis
                val simpleDateFormat =
                    SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
                val dateString = simpleDateFormat.format(dateIn)
                for (i in sms!!.indices) {
                    message += sms[i].messageBody
                }
                Log.i(
                    TAG,
                    "mensaje: $message de $origin ${
                        String.format(
                            "a las %s",
                            dateString
                        )
                    } y recibido en la SIM $simSlotReceptionIndex"
                )

                GlobalScope.launch(Dispatchers.IO) {
                    sendMailSmtp(
                        message
                    )
                }
            }
        }
    }

    private fun sendMailSmtp(
        message: String
    ) {
        try {
            val props = Properties()
            props["mail.smtp.host"] = HOST
            props["mail.smtp.socketFactory.port"] = PORT
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.auth"] = AUTH
            props["mail.smtp.port"] = PORT

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(FROM_ADDRESS, PASSWORD)
                }
            })
            val mm = MimeMessage(session)
            mm.setFrom(InternetAddress(FROM_ADDRESS))
            mm.addRecipient(Message.RecipientType.TO, InternetAddress("rafa.barbeyto@gmail.com"))
            mm.subject = SUBJECT
            mm.setText(message)
            Transport.send(mm)
            Log.d(TAG, "sendEmail: Mensaje enviado")
        } catch (e: Exception) {
            Log.d(TAG, "sendEmailError: ${e.message}")
        }
    }
}