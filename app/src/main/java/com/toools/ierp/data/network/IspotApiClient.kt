package com.toools.ierp.data.network

import com.toools.ierp.data.model.BaseResponse
import com.toools.ierp.data.model.ISpotClientDataResponse
import com.toools.ierp.data.model.ISpotDirectAdsResponse
import com.toools.ierp.data.model.ISpotNotificationsResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IspotApiClient {

    @FormUrlEncoded
    @POST("getappdirectpubli")
    fun directAds(@Field("pid") clientID: String): Response<ISpotDirectAdsResponse>

    @FormUrlEncoded
    @POST("adduser")
    fun addUser(@Field("pid") clientID: String, @Field("sistem") so: String, @Field("token") token: String): Response<BaseResponse>

    @FormUrlEncoded
    @POST("getdataclient")
    fun clientData(@Field("pid") clientID: String): Response<ISpotClientDataResponse>

    @FormUrlEncoded
    @POST("getPushes")
    fun notifications(@Field("pid") clientID: String): Response<ISpotNotificationsResponse>

}
