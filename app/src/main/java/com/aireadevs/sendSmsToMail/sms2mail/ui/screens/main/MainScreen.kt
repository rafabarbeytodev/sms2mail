package com.aireadevs.sendSmsToMail.sms2mail.ui.screens.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Telephony.Sms
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.AUTH
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.FROM_ADDRESS
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.HOST
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.PASSWORD
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.PORT
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.TAG
import com.aireadevs.sendSmsToMail.sms2mail.core.PermissionRequestEffect
import com.aireadevs.sendSmsToMail.sms2mail.domain.BroadcastReceiverService
import com.aireadevs.sendSmsToMail.sms2mail.domain.SmsForegroundService
import java.text.SimpleDateFormat
import java.util.Locale

/*****
 * Proyect: sms2mail
 * Package: com.aireadevs.sendSmsToMail.sms2mail.ui.screens.main
 *
 * Created by Rafael Barbeyto Torrellas on 16/07/2024 at 21:35
 * More info: https://www.linkedin.com/in/rafa-barbeyto/
 *
 * All rights reserved 2024.
 *****/



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(mainVM:MainViewModel) {

    val context = LocalContext.current

    val internetConnection by mainVM.internetConnection.collectAsStateWithLifecycle()
    val mailToSend by mainVM.mailToSend.collectAsStateWithLifecycle()
    var emailToSend by remember { mutableStateOf("") }
    var permissionSmsGranted by remember { mutableStateOf(false) }
    var permissionNotificationGranted by remember { mutableStateOf(false) }

    PermissionRequestEffect(Manifest.permission.RECEIVE_SMS) { granted ->
        permissionSmsGranted = granted
        if(!granted){
            //Mostrar cuadro de dialogo explicando la necesidad de este servicio
            Log.i(TAG, "Permiso SMS DENEGADO")
        }else{
            Log.i(TAG, "Permiso SMS CONCEDIDO")
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PermissionRequestEffect(Manifest.permission.POST_NOTIFICATIONS) { granted ->
            permissionNotificationGranted = granted
            if(!granted){
                //Mostrar cuadro de dialogo explicando la necesidad de este servicio
                Log.i(TAG, "Permiso NOTIFICACIONES DENEGADO")
            }else{
                Log.i(TAG, "Permiso NOTIFICACIONES CONCEDIDO")
            }
        }
    }

    if (permissionSmsGranted) {
        BroadcastReceiverService(systemAction = Sms.Intents.SMS_RECEIVED_ACTION) { receiveIntent ->
            val action = receiveIntent?.action ?: return@BroadcastReceiverService
            if (action == Sms.Intents.SMS_RECEIVED_ACTION) {
                val sms = Sms.Intents.getMessagesFromIntent(receiveIntent)
                val extras = receiveIntent.extras
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
                mainVM.sendMailSmtp(
                    HOST,
                    PORT,
                    AUTH,
                    FROM_ADDRESS,
                    PASSWORD,
                    mailToSend,
                    message
                )
                Toast.makeText(context,"CORREO ENVIADO", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            Text(
                "Conexion Internet: $internetConnection",
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Box {
            TextField(value = emailToSend, onValueChange = {
                emailToSend = it
            }, label = {
                Text(text = "Cuenta de correo")
            },
                singleLine = true

            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Box {
            Button(
                onClick = {
                    if (emailToSend.isNotEmpty()) {
                        mainVM.saveDataStoreString("mailToSend", emailToSend)
                    }
                },
                colors = ButtonColors(Color.White, Color.White, Color.Gray, Color.Gray),
                border = BorderStroke(1.dp, Color.Black),
            ) {
                Text(text = "Save", color = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Box {
            Text(
                text = "Cuenta salvada:\n$mailToSend",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}