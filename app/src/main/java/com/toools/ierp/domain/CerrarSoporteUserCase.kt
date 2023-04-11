package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class CerrarSoporteUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(token: String, idSoporte: String) =
        repository.cerrarSoporte(token, idSoporte)
}