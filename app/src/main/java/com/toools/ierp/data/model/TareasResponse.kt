package com.toools.ierp.data.model

import com.google.gson.annotations.SerializedName
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

data class TareasResponse (
    val status: String? = null,
    val error: String?,
    val tareas: List<Tarea>? = null,

    @SerializedName("server_time_zone")
    val serverTimeZone: String? = null
) {
    fun isOK(): Boolean {

        return status != null && status.lowercase(Locale.ROOT) == "ok" //NON-NLS
    }
}

data class Tarea (
    val idTarea: String? = null,
    val nombreTarea: String? = null,
    val plazo: String? = null,
    val plazoDireccion: String? = null,
    val observaciones: String? = null,
    val idEstado: String? = null,
    val nombreProyecto: String? = null,
    val imgProyecto: String? = null,
    val imgMiniProyecto: String? = null,
    val nombreAutor: String? = null,
    val imgAutor: String? = null,
    val idAutor: String? = null,
    val idProyecto: String? = null,
    val estados: List<Estado>? = null
) {
    fun isAceptada(): Boolean {
        return estados?.size?.let{ it > 0 } ?: run{ false }
    }

    fun getPlazoFormateado(): String {

        try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formatResult = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            plazoDireccion?.let { plazo ->
                val date = format.parse(plazo)
                return formatResult.format(date)?.let {
                    it
                } ?: run {
                    ""
                }
            }
        }catch (exception: Exception){

        }

        return ""
    }
}

data class Estado (
    val id: String? = null,
    val idEstado: String? = null,
    val obsercacion: String? = null,
    val fecha: String? = null
)
