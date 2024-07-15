package com.aireadevs.sendSmsToMail.sms2mail.data.datastore

import com.google.gson.annotations.SerializedName

/*****
 * Proyect: sms2mail
 * Package: com.aireadevs.sendSmsToMail.sms2mail.data.datastore
 *
 * Created by Rafael Barbeyto Torrellas on 11/07/2024 at 15:36
 * More info: https://www.linkedin.com/in/rafa-barbeyto/
 *
 * All rights reserved 2024.
 *****/
data class DataStoreEntity(
    @SerializedName("mailDeveloper") var mailDeveloper: String = "",
    @SerializedName("mailToSend") var mailToSend: String = "",
    @SerializedName("showFiveStars") var showFiveStars: Boolean = false,
    @SerializedName("numberOfVisits") var numberOfVisits: Int = 0,
    @SerializedName("NotShowFiveStars") var notshowfivestars: Boolean = false
)
