package com.toools.ierp.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.data.model.BaseResponse
import com.toools.ierp.domain.AddUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel  @Inject constructor(private val addUserUseCase: AddUserUseCase): ViewModel() {

    val addUserLiveData: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun addUser(token: String) {
        viewModelScope.launch {
            addUserLiveData.value = Resource.loading()
            val response = addUserUseCase.invoke(token)
            if (response != null && response.isOK()) {
                addUserLiveData.value = Resource.success(response)
            } else {
                addUserLiveData.value = Resource.error(ErrorHelper.iSpotAddUserError)
            }
        }
    }
}