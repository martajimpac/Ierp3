package com.toools.ierp.data.model

import com.google.gson.annotations.SerializedName
import java.util.*
data class ProyectosResponse (
    val proyectos: List<Proyecto>?,

    @SerializedName("server_time_zone")
    val serverTimeZone: String
): BaseResponse()
data class Proyecto (
    val id: String?,
    val nombre: String?,
    val descripcion: String?,
    val miniatura: String?,
    val imagen: String?,
    val codigo: String?,
    val usuarios: List<Usuario>
)

data class Usuario (
    val id: String?,
    val username: String?,
    val nombre: String?,
    val email: String?,
    val imagen: String?
)
