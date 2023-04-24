package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class TareasUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(token: String) = repository.tareas(token)
}
