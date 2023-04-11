package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class LoginUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(client: String, usuario: String, password: String) =
        repository.login(client, usuario, password)
}