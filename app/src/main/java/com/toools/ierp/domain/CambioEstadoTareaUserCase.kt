package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class CambioEstadoTareaUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(token: String, idTarea: String, idEstado:String, observacion:String) =
        repository.cambioEstadoTarea(token, idTarea, idEstado, observacion)
}
