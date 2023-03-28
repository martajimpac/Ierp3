package com.toools.ierp.ui.login


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.Repository
import com.toools.ierp.data.model.MomentosResponse
import com.toools.ierp.data.model.BaseResponse
import com.toools.ierp.data.model.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel  @Inject constructor(private val repository: Repository): ViewModel() {

    val loginLiveData: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val momentosLiveData: MutableLiveData<Resource<MomentosResponse>> = MutableLiveData()
    val addTokenFirebaseLiveData: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun login(client: String, user: String, password: String){

        viewModelScope.launch {
            loginLiveData.value = Resource.loading()
            val response = repository.login(client,user,password)
            if (response != null) {
                if (response.isOK() && response.userId != null) {
                    loginLiveData.value = Resource.success(response)
                } else {
                    if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                        loginLiveData.value = Resource.error(ErrorHelper.notSession)
                    }else{
                        loginLiveData.value = Resource.error(ErrorHelper.loginNoValido)
                    }
                }
            }else{
                loginLiveData.value = Resource.error(ErrorHelper.loginError)
            }
        }
    }

    fun momentos(usuario: String){

        viewModelScope.launch {
            momentosLiveData.value = Resource.loading()
            val response = repository.momentos(ConstantHelper.clientREST,usuario)

            if(response != null) {
                if(response.isOK()) {
                    momentosLiveData.value = Resource.success(response)
                }else {
                    if(response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED){
                        momentosLiveData.value = Resource.error(ErrorHelper.notSession)
                    }else {
                        momentosLiveData.value = Resource.error(ErrorHelper.momentosError)
                    }
                }
            }else{
                momentosLiveData.value = Resource.error(ErrorHelper.momentosError)
            }
        }
    }
    fun addTokenFirebase(token: String, tokenFirebase: String){

        viewModelScope.launch {
            addTokenFirebaseLiveData.value = Resource.loading()
            val response = repository.addTokenFirebase(ConstantHelper.clientREST, token, tokenFirebase)
            addTokenFirebaseLiveData.value = Resource.success(response)
        }

    }
}