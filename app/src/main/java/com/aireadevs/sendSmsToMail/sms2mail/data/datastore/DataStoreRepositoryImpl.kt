package com.aireadevs.sendSmsToMail.sms2mail.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.USER_PREFERENCES_NAME
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.MAIL_DEVELOPER
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.NOT_SHOW_FIVE_STARS
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.NUMBER_VISITS
import com.aireadevs.sendSmsToMail.sms2mail.core.Constants.SHOW_FIVE_STARS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject


/*****
 * Proyect: sms2mail
 * Package: com.aireadevs.sendSmsToMail.sms2mail.data.datastore
 *
 * Created by Rafael Barbeyto Torrellas on 11/07/2024 at 15:58
 * More info: https://www.linkedin.com/in/rafa-barbeyto/
 *
 * All rights reserved 2024.
 *****/

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)

class DataStoreRepositoryImpl @Inject constructor(
    private val context: Context
) : DataStoreRepository {
    override suspend fun putString(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun putInt(key: String, value: Int) {
        val preferencesKey = intPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        val preferencesKey = booleanPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun getDataStore(): Flow<DataStoreEntity?> {
        return context.dataStore.data
            .map { preferences ->
                DataStoreEntity(
                    mailDeveloper = preferences[stringPreferencesKey(MAIL_DEVELOPER)].orEmpty(),
                    showFiveStars = preferences[booleanPreferencesKey(SHOW_FIVE_STARS)] ?: true,
                    numberOfVisits = preferences[intPreferencesKey(NUMBER_VISITS)] ?: 0,
                    notshowfivestars = preferences[booleanPreferencesKey(NOT_SHOW_FIVE_STARS)] ?: true,
                )
            }.catch { error ->
                Log.i(Constants.TAG, "ACCESS ERROR DATASTORE: ${error.message}")
            }
    }

}