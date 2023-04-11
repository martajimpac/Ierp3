package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class TareasAsignadasUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(token: String) = repository.tareasAsignadas(token)
}
