package com.toools.ierp.data.model

import com.google.gson.annotations.SerializedName
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

data class TareasAsignadasResponse (
    val status: String? = null,
    val error: String?,
    val tareas: List<TareaAsignada>? = null,

    @SerializedName("server_time_zone")
    val serverTimeZone: String? = null
) {
    fun isOK(): Boolean {
        return status != null && status.lowercase(Locale.ROOT) == "ok" //NON-NLS
    }
}

data class TareaAsignada (
    val idTarea: String? = null,
    val nombreTarea: String? = null,
    val plazo: String? = null,
    val plazoDireccion: String? = null,
    val observaciones: String? = null,
    val idEstado: String? = null,
    val nombreProyecto: String? = null,
    val imgProyecto: String? = null,
    val imgMiniProyecto: String? = null,
    val nombreEmpleado: String? = null,
    val imgEmpleado: String? = null,
    val idEmpleado: String? = null,
    val idProyecto: String? = null,
    val estados: List<Estado>? = null
)  {
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