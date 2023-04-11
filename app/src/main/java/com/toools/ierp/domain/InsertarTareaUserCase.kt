package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class InsertarTareaUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(
        token: String,
        idProyecto: String,
        idEmpleado: String,
        titulo: String,
        descripcion: String,
        plazo: String
    ) = repository.insertarTarea(token, idProyecto, idEmpleado, titulo, descripcion, plazo)
}
