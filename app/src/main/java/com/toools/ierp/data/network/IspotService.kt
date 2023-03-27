package com.toools.ierp.data.network

import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.BaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IspotService @Inject constructor(private val api: IspotApiClient) {

    suspend fun addUser(token: String): BaseResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.addUser(ConstantHelper.iSpotId, ConstantHelper.iSpotSystem, token)
            response.body()
        }
    }

}