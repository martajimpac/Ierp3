package com.toools.ierp.data

import com.toools.ierp.data.model.*
import com.toools.ierp.data.network.IerpService
import com.toools.ierp.data.network.IspotService
import javax.inject.Inject

class Repository @Inject constructor(private val ispotService: IspotService, private val ierpService: IerpService) {

    //IERP
    suspend fun login(client: String, usuario: String, password: String): LoginResponse? {
        return ierpService.login(client, usuario, password)
    }
    suspend fun momentos(client: String, usuario: String): MomentosResponse? {
        return ierpService.momentos(client, usuario)
    }
    suspend fun addTokenFirebase(client: String, token: String, tokenFirebase: String): BaseResponse? {
        return ierpService.addTokenFirebase(client,token,tokenFirebase)
    }

    //ISPOT
    suspend fun addUser(tokenFirebase: String): BaseResponse? {
        return ispotService.addUser(tokenFirebase)
    }

    suspend fun clientData(clientID: String): ISpotClientDataResponse? {
        return ispotService.clientData(clientID)
    }

    suspend fun directAds(clientID: String): ISpotDirectAdsResponse? {
        return ispotService.directAds(clientID)
    }

    suspend fun notifications(clientID: String): ISpotNotificationsResponse? {
        return ispotService.notifications(clientID)
    }

    //Guardar el usuario
    companion object{
        var usuario: LoginResponse? = null
    }

}