package com.aireadevs.sendSmsToMail.sms2mail.ui.screens.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.SUBJECT
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.TAG
import com.aireadevs.sendSmsToMail.sms2mail.data.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Properties
import javax.inject.Inject
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/*****
 * Proyect: sms2mail
 * Package: com.aireadevs.sendSmsToMail.sms2mail.ui.screens.main
 *
 * Created by Rafael Barbeyto Torrellas on 11/07/2024 at 16:36
 * More info: https://www.linkedin.com/in/rafa-barbeyto/
 *
 * All rights reserved 2024.
 *****/
@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: DataStoreRepository,
) : ViewModel() {

    private val _splashShow = MutableStateFlow(true)
    val splashShow: StateFlow<Boolean> = _splashShow

    private val _internetConnection = MutableStateFlow(false)
    val internetConnection: StateFlow<Boolean> = _internetConnection

    private val _mailToSend = MutableStateFlow("")
    val mailToSend: StateFlow<String> = _mailToSend

    private val _mailDeveloper = MutableStateFlow("")
    val mailDeveloper: StateFlow<String> = _mailDeveloper

    private val _showFiveStars = MutableStateFlow(false)
    val showFiveStars: StateFlow<Boolean> = _showFiveStars

    private val _notshowfivestars = MutableStateFlow(false)
    val notshowfivestars: StateFlow<Boolean> = _notshowfivestars

    private val _isSoundActivated = MutableStateFlow(false)
    val isSoundActivated: StateFlow<Boolean> = _isSoundActivated

    private val _numberOfVisits = MutableStateFlow(0)
    val numberOfVisits: StateFlow<Int> = _numberOfVisits


    fun hideSplash() {
        viewModelScope.launch {
            //Aseguramos la visializacion de 1sg si la carga es muy rapida
            //delay(1000L)
            _splashShow.value = false
        }
    }

    fun checkInternetConnection(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Verificar el estado de la conexión inmediatamente después de registrar el NetworkCallback
        val network = connectivityManager.activeNetwork
        val capabilities =
            connectivityManager.getNetworkCapabilities(network)// No hay conexión a Internet disponible

        // Hay una conexión a Internet disponible
        _internetConnection.value =
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))

        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {

            override fun onUnavailable() {
                super.onUnavailable()
                _internetConnection.value = false
            }

            override fun onAvailable(network: Network) {
                // La conexión a Internet está disponible
                _internetConnection.value = true
            }

            override fun onLost(network: Network) {
                // La conexión a Internet se perdió
                _internetConnection.value = false
            }
        })
    }

    fun saveDataStoreString(field: String, value: String) {
        viewModelScope.launch {
            dataStore.putString(field, value)
        }
    }

    fun getDataStoreFields() {
        viewModelScope.launch {
            dataStore.getDataStore().collect { data ->
                if (data != null) {
                    with(data) {
                        _mailDeveloper.value = mailDeveloper
                        _mailToSend.value = mailToSend
                        _numberOfVisits.value = numberOfVisits
                        _showFiveStars.value = showFiveStars
                        _notshowfivestars.value = notshowfivestars
                    }
                }
            }
        }
    }

    fun sendMailSmtp(
        host: String,
        port: String,
        auth: String,
        fromAddress: String,
        password: String,
        toAddress: String,
        message: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val props = Properties()
                props["mail.smtp.host"] = host
                props["mail.smtp.socketFactory.port"] = port
                props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
                props["mail.smtp.auth"] = auth
                props["mail.smtp.port"] = port

                Log.d(TAG, "sendEmail: one")
                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(fromAddress, password)
                    }
                })
                val mm = MimeMessage(session)
                mm.setFrom(InternetAddress(fromAddress))
                mm.addRecipient(Message.RecipientType.TO, InternetAddress(toAddress))
                mm.subject = SUBJECT
                mm.setText(message)
                Log.d(TAG, "sendEmail: two")
                Transport.send(mm)
                Log.d(TAG, "sendEmail: three")
            } catch (e: Exception) {
                Log.d(TAG, "sendEmail: ${e.message}")
            }
        }
    }
}