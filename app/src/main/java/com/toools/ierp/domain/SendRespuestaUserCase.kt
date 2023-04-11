package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class SendRespuestaUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(token: String, idSoporte: String, respuesta: String) =
        repository.sendRespuesta(token, idSoporte, respuesta)
}