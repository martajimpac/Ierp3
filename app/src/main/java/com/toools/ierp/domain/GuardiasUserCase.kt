package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class GuardiasUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(usuario: String, dia: Int, mes: Int, ano: Int) =
        repository.guardias(usuario, dia, mes, ano)
}