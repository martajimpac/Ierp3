package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class AddGuardiaUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(usuario: String, fecha: String, descripcion: String, tipo: Int) =
        repository.addGuardia(usuario, fecha, descripcion, tipo)
}
