package com.toools.ierp.data.network

import com.toools.ierp.data.model.BaseResponse
import com.toools.ierp.data.model.LoginResponse
import com.toools.ierp.data.model.MomentosResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IerpService @Inject constructor(private val api: IerpApiClient) {

    suspend fun login(client: String, usuario: String, password: String): LoginResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.loginIERP(client,usuario,password)
            response.body()
        }
    }

    suspend fun momentos(client: String, usuario: String): MomentosResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.momentos(client,usuario)
            response.body()
        }
    }

    suspend fun addTokenFirebase(client: String, token: String, tokenFirebase: String): BaseResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.addTokenFirebase(client,token,tokenFirebase)
            response.body()
        }
    }
}