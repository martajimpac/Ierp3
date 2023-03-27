package com.toools.ierp.data.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

class GuardiasResponse: BaseResponse() {

    @SerializedName("server_time_zone")
    var timeZone: String? = null

    var actuaciones: MutableList<Guardias> = mutableListOf()

    class Guardias {
        var momento: String? = null
        var tipo: String = "0"
        var descripcion: String? = null

        companion object {
            const val entrada = "1"
            const val salida = "0"

            const val entradaInt = 1
            const val salidaInt = 0
        }


        fun getHoraToString() : String{

            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formatResult = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

            return formatResult.format(format.parse(momento))

        }
    }
}