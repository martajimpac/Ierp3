package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class MomentosUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(usuario: String) = repository.momentos(usuario)
}