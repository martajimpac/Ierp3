package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class AddUserUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(tokenFirebase: String) = repository.addUser(tokenFirebase)
}
