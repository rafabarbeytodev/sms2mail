package com.aireadevs.sendSmsToMail.sms2mail.ui.screens.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.TAG
import com.aireadevs.sendSmsToMail.sms2mail.data.datastore.DataStoreEntity
import com.aireadevs.sendSmsToMail.sms2mail.data.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    private val dataStore: DataStoreRepository
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

    fun saveDataStoreString(field: String, value:String){
        viewModelScope.launch {
            dataStore.putString(field,value)
        }
    }

    fun getDataStoreFields(){
        viewModelScope.launch {
            dataStore.getDataStore().collect { data ->
                if (data != null) {
                    with(data){
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

}