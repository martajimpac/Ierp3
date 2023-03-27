package com.toools.ierp.data.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class ISpotNotificationsResponse(val status: String, val error: String?, @SerializedName("mensajes") val messages: List<ISpotNotification>) {

    fun isOK(): Boolean {

        return status.lowercase(Locale.ROOT) == "ok"
    }
}

data class ISpotNotification(@SerializedName("mensaje") val message: String, @SerializedName("fecha") val date: String) {

    fun formattedDate(): String {

        val dft = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dft2 = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        return dft2.format(dft.parse(date))
    }
}