package com.aireadevs.sendSmsToMail.sms2mail.ui.screens.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aireadevs.sendSmsToMail.sms2mail.domain.SmsForegroundService
import com.aireadevs.sendSmsToMail.sms2mail.ui.theme.Sms2MailTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainVM: MainViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.O)
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
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    val serviceIntent = Intent(context, SmsForegroundService::class.java)
                    context.startForegroundService(serviceIntent)
                }

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
                MainScreen(mainVM)
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