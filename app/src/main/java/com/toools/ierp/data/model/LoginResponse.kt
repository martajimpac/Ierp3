package com.toools.ierp.data.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

class LoginResponse : BaseResponse() {
    var token: String? = null
    @SerializedName("id_usuario")
    var userId: String? = null
    var id_rol: String? = null
    var nombre: String? = null
    var username: String? = null
    var momentos: MutableList<Momentos> = mutableListOf()
    var permitir_no_localizacion: String? = null
    @SerializedName("zona_horaria_servidor")
    var zonaHoraria: String? = null
    var roles: MutableList<Rol> = mutableListOf()
    var centros_trabajo: MutableList<Centro> = mutableListOf()
    var id_centro_trabajo: String? = null

    class Rol(
        val id_rol: String?,
        @SerializedName("0")
        val cero: String?
    )

    class Centro(
        val ct_id: String?,
        val ct_nombre: String?,
        val ct_descripcion: String?,
        val ct_direccion: String?,
        val ct_latitud: String?,
        val ct_longitud: String?,
        val ct_distancia_fichajes: String?
    )

    class Momentos {

        var momento: String? = null
        var tipo: String? = null

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