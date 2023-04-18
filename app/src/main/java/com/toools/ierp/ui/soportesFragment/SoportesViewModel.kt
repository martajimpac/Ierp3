package com.toools.ierp.ui.soportesFragment


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.data.Repository
import com.toools.ierp.data.model.BaseResponse
import com.toools.ierp.data.model.ProyectosResponse
import com.toools.ierp.data.model.SoportesResponse
import com.toools.ierp.domain.AsignarSoporteUserCase
import com.toools.ierp.domain.ProyectosUserCase
import com.toools.ierp.domain.SoportesUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoportesViewModel @Inject constructor(private val proyectosUserCase: ProyectosUserCase, private val soportesUserCase: SoportesUserCase, private val asignarSoporteUserCase: AsignarSoporteUserCase): ViewModel() {

    val proyectosLiveData: MutableLiveData<Resource<ProyectosResponse>> = MutableLiveData()
    val soportesLiveData: MutableLiveData<Resource<SoportesResponse>> = MutableLiveData()
    val asignarSoportesLiveData: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun proyectos(){
        viewModelScope.launch {
            proyectosLiveData.value = Resource.loading()
            Repository.usuario?.token?.let{ token ->
                val response = proyectosUserCase.invoke(token)
                if (response != null) {
                    if (response.isOK()) {
                        proyectosLiveData.value = Resource.success(response)
                    } else if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                        proyectosLiveData.value = Resource.success(response)
                    } else {
                        proyectosLiveData.value = Resource.error(ErrorHelper.proyectosError)
                    }
                } else {
                    proyectosLiveData.value = Resource.error(ErrorHelper.proyectosError)
                }
            }
        }
    }

    fun soportes(){
        viewModelScope.launch {
            soportesLiveData.value = Resource.loading()
            Repository.usuario?.token?.let{ token ->
                val response = soportesUserCase.invoke(token)
                if (response != null) {
                    if (response.isOK()) {
                        soportesLiveData.value = Resource.success(response)
                    } else if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                        soportesLiveData.value = Resource.success(response)
                    } else {
                        soportesLiveData.value = Resource.error(ErrorHelper.soportesError)
                    }
                } else {
                    proyectosLiveData.value = Resource.error(ErrorHelper.soportesError)
                }
            }
        }
    }

    fun asignarSoportes(idSoporte: String){
        viewModelScope.launch {
            asignarSoportesLiveData.value = Resource.loading()
            Repository.usuario?.token?.let{ token ->
                val response = asignarSoporteUserCase.invoke(token, idSoporte)
                if (response != null) {
                    if (response.isOK()) {
                        asignarSoportesLiveData.value = Resource.success(response)
                    } else if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                        asignarSoportesLiveData.value = Resource.success(response)
                    } else {
                        asignarSoportesLiveData.value = Resource.error(ErrorHelper.asignarSoporteError)
                    }
                } else {
                    asignarSoportesLiveData.value = Resource.error(ErrorHelper.asignarSoporteError)
                }
            }
        }
    }
}