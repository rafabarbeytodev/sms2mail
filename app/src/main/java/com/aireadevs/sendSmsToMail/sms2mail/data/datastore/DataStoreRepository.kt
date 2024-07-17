package com.aireadevs.sendSmsToMail.sms2mail.data.datastore

import kotlinx.coroutines.flow.Flow

/*****
 * Proyect: sms2mail
 * Package: com.aireadevs.sendSmsToMail.sms2mail.data.datastore
 *
 * Created by Rafael Barbeyto Torrellas on 11/07/2024 at 15:57
 * More info: https://www.linkedin.com/in/rafa-barbeyto/
 *
 * All rights reserved 2024.
 *****/
interface DataStoreRepository {
    suspend fun putString(key: String, value: String)
    suspend fun putInt(key: String, value: Int)
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun getDataStore(): Flow<DataStoreEntity?>

}