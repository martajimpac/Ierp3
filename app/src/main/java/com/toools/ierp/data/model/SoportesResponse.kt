package com.toools.ierp.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

data class SoportesResponse (
    val soportes: List<Soporte>? = null,

    @SerializedName("server_time_zone")
    val serverTimeZone: String
): BaseResponse()

data class Soporte (
    val idIncidencia: String?,
    val idProyecto: String?,
    val nombreProyecto: String?,
    val imgMiniProyecto: String?,
    val titulo: String?,
    val descripcion: String?,
    val fecha: String?,
    var estado: String?,
    val idUserAsignado: String?,
    val emailUserIncidencia: String?,
    val idUsuario: String?,
    val nombreAsignado: String?,
    val aliasAsignado: String?,
    val imagenAsignado: String?
) : Serializable {
    companion object{
        const val sinAsignar = "0"
    }

    fun getFechaFormateada(): String {

        try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formatResult = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

            fecha?.let { fecha ->
                val date = format.parse(fecha)
                return formatResult.format(date.run{//todo ver si esto sigue funcionado TODO
                    ""
                })
            }
        }catch (_: Exception){

        }

        return ""
    }
}