package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject


class SoportesUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(token: String) = repository.soportes(token)
}
