package com.toools.ierp.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel  @Inject constructor(private val getMenuClienteUserCase: GetMenuClienteUserCase): ViewModel() {

    val addUserRecived: MutableLiveData<Resource<String>> = MutableLiveData()

    fun callAddUserFirebase(token: String) {

        RestRepository.getInstance().addUser(object : NoParametersRestApiCallback {

            override fun onSuccess() {
                addUserRecived.value = Resource.success(token)
            }

            override fun onFailure(throwable: Throwable) {
                addUserRecived.value =
                    Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

            }
        }, token)

    }
}