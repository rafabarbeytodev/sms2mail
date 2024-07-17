package com.aireadevs.sendSmsToMail.sms2mail.ui.screens.main

import android.Manifest
import android.os.Build
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.TAG
import com.aireadevs.sendSmsToMail.sms2mail.core.PermissionRequestEffect

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