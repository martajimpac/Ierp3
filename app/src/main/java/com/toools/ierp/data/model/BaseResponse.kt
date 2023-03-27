package com.toools.ierp.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

open class BaseResponse {

    @SerializedName("status") val status: String? = null
    @SerializedName("error") val error: String? = null

    fun isOK(): Boolean {
        return status?.lowercase(Locale.getDefault()) == "ok"
    }
}