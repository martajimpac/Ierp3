package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class MomentosDiaUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(usuario: String, dia: Int, mes: Int, ano: Int) =
        repository.momentosDia(usuario, dia, mes, ano)
}