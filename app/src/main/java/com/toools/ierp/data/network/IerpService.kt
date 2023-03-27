package com.toools.ierp.data.network

import com.toools.ierp.data.model.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IerpService @Inject constructor(private val api: IerpApiClient) {

    suspend fun getLogin(client: String, usuario: String, password: String): LoginResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.loginIERP(client,usuario,password)
            response.body()
        }
    }
}