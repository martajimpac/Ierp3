package com.toools.ierp.di

import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.network.IerpApiClient
import com.toools.ierp.data.network.IspotApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

//sirve para proveer dependencias de clases que contienen interfaces o de librerias
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): OkHttpClient {

        val httpBuilder = OkHttpClient.Builder()
        return httpBuilder.readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideIerpApiClient(cliente: OkHttpClient): IerpApiClient{
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ConstantHelper.ierpBaseUrl) //urlIerp urlIspot
            .client(cliente)
            .build().create(IerpApiClient::class.java)
    }

    @Singleton
    @Provides
    fun provideIspotApiClient(cliente: OkHttpClient): IspotApiClient {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ConstantHelper.iSpotBaseURL) //urlIerp urlIspot
            .client(cliente)
            .build().create(IspotApiClient::class.java)
    }
}