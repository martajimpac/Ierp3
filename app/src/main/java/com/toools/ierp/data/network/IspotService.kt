package com.toools.ierp.data.network

import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.BaseResponse
import com.toools.ierp.data.model.ISpotClientDataResponse
import com.toools.ierp.data.model.ISpotDirectAdsResponse
import com.toools.ierp.data.model.ISpotNotificationsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IspotService @Inject constructor(private val api: IspotApiClient) {

    suspend fun addUser(token: String): BaseResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.addUser(ConstantHelper.iSpotClientId, ConstantHelper.iSpotSystem, token)
            response.body()
        }
    }

    suspend fun clientData(clientID: String): ISpotClientDataResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.clientData(clientID)
            response.body()
        }
    }

    suspend fun directAds(clientID: String): ISpotDirectAdsResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.directAds(clientID)
            response.body()
        }
    }

    suspend fun notifications(clientID: String): ISpotNotificationsResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.notifications(clientID)
            response.body()
        }
    }
}