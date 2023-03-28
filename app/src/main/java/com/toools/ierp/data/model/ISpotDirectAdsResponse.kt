package com.toools.ierp.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ISpotDirectAdsResponse (@SerializedName("imagenes") val ads: List<ISpotDirectAd>?): BaseResponse() {
}

enum class ISpotAdType {

    SplashTop, SplashBottom, Main, Circular
}

data class ISpotDirectAd (@SerializedName("enlace") val link: String, @SerializedName("id_tipo") val type: String, @SerializedName("img") val image: String, @SerializedName("titulo") val title: String) {

    fun isType(typeToCheck: ISpotAdType): Boolean {

        return when (typeToCheck) {
            ISpotAdType.SplashTop -> type == "5"
            ISpotAdType.SplashBottom -> type == "4"
            ISpotAdType.Main -> type == "2"
            ISpotAdType.Circular -> type == "1"
        }
    }
}
