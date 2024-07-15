package com.aireadevs.sendSmsToMail.sms2mail.ui.screens.main

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.TAG
import com.aireadevs.sendSmsToMail.sms2mail.ui.theme.Sms2MailTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainVM: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                mainVM.splashShow.value
            }
            setOnExitAnimationListener { screen ->
                val fadeAnim = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.ALPHA,
                    1f,
                    0f
                )
                fadeAnim.interpolator = AccelerateInterpolator()
                fadeAnim.duration = 800L
                fadeAnim.doOnEnd { screen.remove() }
                fadeAnim.start()
            }

        }

        initUI()

        setContent {
            Sms2MailTheme {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.light(
                        MaterialTheme.colorScheme.onPrimary.toArgb(),
                        MaterialTheme.colorScheme.onPrimary.toArgb()
                    ),
                    navigationBarStyle = SystemBarStyle.light(
                        MaterialTheme.colorScheme.background.toArgb(),
                        MaterialTheme.colorScheme.background.toArgb()
                    )
                )
                CheckInternetConnection()
            }
        }
    }

    @Preview
    @Composable
    private fun CheckInternetConnection() {

        val internetConnection by mainVM.internetConnection.collectAsStateWithLifecycle()
        val mailToSend by mainVM.mailToSend.collectAsStateWithLifecycle()
        var emailToSend by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Log.i(TAG, "Conexion Internet: $internetConnection")
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
                Text(text = "Cuenta salvada:\n$mailToSend", color = MaterialTheme.colorScheme.primary)
            }
        }
    }

    private fun initUI() {
        //Cerramos Splash
        mainVM.hideSplash()
        //CHECK INTERNET CONNEXION
        mainVM.checkInternetConnection(this)
        //flow con DataStore
        mainVM.getDataStoreFields()
    }
}