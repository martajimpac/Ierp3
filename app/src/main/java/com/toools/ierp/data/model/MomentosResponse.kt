package com.toools.ierp.data.model

import com.google.gson.annotations.SerializedName

class MomentosResponse : BaseResponse(){
    var momentos : MutableList<LoginResponse.Momentos> = mutableListOf()
    @SerializedName("server_time_zone")
    var timeZone : String? = null
}