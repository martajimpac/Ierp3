package com.toools.ierp.domain

import com.toools.ierp.data.Repository
import javax.inject.Inject

class ClientDataUserCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(clientID: String) = repository.clientData(clientID)
}
