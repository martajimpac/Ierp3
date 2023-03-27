package com.toools.ierp.ui.main

/*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.ierp.MomentosResponse
import com.toools.ierp.helpers.rest.AppException
import com.toools.ierp.helpers.rest.RestApiCallback
import com.toools.ierp.helpers.rest.RestRepository

class MainViewModel : ViewModel() {

    val momentosRecived: MutableLiveData<Resource<MomentosResponse>> = MutableLiveData()

    fun callLoginIerp(token: String){

        RestRepository.getInstance().momentos(token, object : RestApiCallback<MomentosResponse> {


            override fun onSuccess(response: MomentosResponse) {
                momentosRecived.value = Resource.success(response)
            }

            override fun onFailure(throwable: Throwable) {
                momentosRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

            }
        })

    }
}*/