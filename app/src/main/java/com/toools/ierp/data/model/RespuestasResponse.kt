package com.toools.ierp.data.model

import com.google.gson.annotations.SerializedName
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

data class RespuestasResponse (
    val status: String?,
    val error: String?,
    val respuestas: List<Respuesta>,

    @SerializedName("server_time_zone")
    val serverTimeZone: String
) {
    fun isOK(): Boolean {
        return status != null && status.lowercase(Locale.getDefault()) == "ok" //NON-NLS
    }
}

data class Respuesta (
    val idRespuesta: String?,
    val respuesta: String?,
    val fecha: String?,
    val idAutor: String?,
    val emailAutor: String?,
    val nombreUsuario: String? = null,
    val aliasUsuario: String? = null,
    val imagenUsuario: String?
) {
    fun getFechaFormateada(): String {

        try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formatResult = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

            fecha?.let { fecha ->
                val date = format.parse(fecha)
                return date?.let {
                    formatResult.format(it)?.let {
                        it
                    }
                } ?: run {
                    ""
                }
            }
        }catch (exception: Exception){

        }

        return ""
    }
}