package com.toools.ierp.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.RestBaseObject
import com.toools.ierp.entities.ierp.LoginResponse
import com.toools.ierp.entities.ierp.MomentosResponse
import com.toools.ierp.helpers.rest.AppException
import com.toools.ierp.helpers.rest.RestApiCallback
import com.toools.ierp.helpers.rest.RestRepository

class LoginViewModel : ViewModel() {

    val loginIerpRecived: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()

    fun callLoginIerp(client: String, user: String, password: String){

        RestRepository.getInstance().login(client, user, password, object : RestApiCallback<LoginResponse> {


            override fun onSuccess(response: LoginResponse) {
                loginIerpRecived.value = Resource.success(response)
            }

            override fun onFailure(throwable: Throwable) {
                loginIerpRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

            }
        })
    }

    val momentosRecived: MutableLiveData<Resource<MomentosResponse>> = MutableLiveData()

    fun callMomentos(token: String){

        RestRepository.getInstance().momentos(token, object : RestApiCallback<MomentosResponse> {


            override fun onSuccess(response: MomentosResponse) {
                momentosRecived.value = Resource.success(response)
            }

            override fun onFailure(throwable: Throwable) {
                momentosRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

            }
        })

    }

    val sendTokenFirebaseRecived: MutableLiveData<Resource<RestBaseObject>> = MutableLiveData()
    fun sendTokenFirebase(token: String, tokenFirebase: String){

        RestRepository.getInstance().addTokenFirebase(token, tokenFirebase, object : RestApiCallback<RestBaseObject> {

            override fun onSuccess(response: RestBaseObject) {
                sendTokenFirebaseRecived.value = Resource.success(response)
            }

            override fun onFailure(throwable: Throwable) {

            }
        })

    }
}