package com.toools.ierp.ui.main


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.Repository
import com.toools.ierp.data.model.MomentosResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    val momentosLiveData: MutableLiveData<Resource<MomentosResponse>> = MutableLiveData()

    //todo porque llama aqui a momentos de nuevo?
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
}