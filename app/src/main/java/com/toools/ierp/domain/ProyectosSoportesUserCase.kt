package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject


class ProyectosSoportesUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(token: String) = repository.proyectosSoportes(token)
}