package com.aireadevs.sendSmsToMail.sms2mail.ui.screens.main

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.TAG
import com.aireadevs.sendSmsToMail.sms2mail.ui.theme.Sms2mailTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainVM: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

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
            Sms2mailTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val internetConnection by mainVM.internetConnection.collectAsStateWithLifecycle()
                    Log.i(TAG,"Conexion Internet: $internetConnection")

                    Text("Conexion Internet: $internetConnection", color = Color.Black)
                }
            }
        }
    }

    private fun initUI(){
        //Cerramos Splash
        mainVM.hideSplash()
        //CHECK INTERNET CONNEXION
        mainVM.checkInternetConnection(this)
    }
}