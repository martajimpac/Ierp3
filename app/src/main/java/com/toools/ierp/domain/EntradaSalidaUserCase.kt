package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class EntradaSalidaUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(
        token: String,
        entrar: Int,
        latitud: Double,
        longitud: Double,
        comments: String,
        descripcion: String
    ) = repository.entradaSalida(token, entrar, latitud, longitud, comments, descripcion)
}