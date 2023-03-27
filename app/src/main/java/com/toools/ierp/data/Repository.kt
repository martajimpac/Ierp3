package com.toools.ierp.data

import com.toools.ierp.data.model.BaseResponse
import com.toools.ierp.data.network.IerpService
import com.toools.ierp.data.network.IspotService
import javax.inject.Inject

class Repository @Inject constructor(private val ispotService: IspotService, private val ierpService: IerpService) {

    //IERP


    //ISPOT
    suspend fun addUser(tokenFirebase: String): BaseResponse? {
        return ispotService.addUser(tokenFirebase)
    }

}