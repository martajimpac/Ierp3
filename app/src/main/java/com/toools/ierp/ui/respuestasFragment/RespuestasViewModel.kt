package com.toools.ierp.ui.respuestasFragment


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.data.Repository
import com.toools.ierp.data.model.BaseResponse
import com.toools.ierp.data.model.RespuestasResponse
import com.toools.ierp.domain.AsignarSoporteUserCase
import com.toools.ierp.domain.CerrarSoporteUserCase
import com.toools.ierp.domain.RespuestasUserCase
import com.toools.ierp.domain.SendRespuestaUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RespuestasViewModel @Inject constructor(private val respuestasUserCase: RespuestasUserCase, private val asignarSoportesUserCase: AsignarSoporteUserCase,
                                              private val cerrarSoporteUserCase: CerrarSoporteUserCase, private val sendRespuestaUserCase: SendRespuestaUserCase): ViewModel() {

    val respuestasLiveData: MutableLiveData<Resource<RespuestasResponse>> = MutableLiveData()
    val asignarSoportesLiveData: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
    val cerrarSoporteLiveData: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
    val sendRespuestaLiveData: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun respuestas(idSoporte: String){
        viewModelScope.launch {
            respuestasLiveData.value = Resource.loading()
            Repository.usuario?.token?.let{ token ->
                val response = respuestasUserCase.invoke(token,idSoporte)
                if (response != null) {
                    if (response.isOK()) {
                        respuestasLiveData.value = Resource.success(response)
                    } else if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                        respuestasLiveData.value = Resource.success(response)
                    } else {
                        respuestasLiveData.value = Resource.error(ErrorHelper.respuestasError)
                    }
                } else {
                    respuestasLiveData.value = Resource.error(ErrorHelper.respuestasError)
                }
            }
        }
    }

    fun asignarSoportes(idSoporte: String){
        viewModelScope.launch {
            asignarSoportesLiveData.value = Resource.loading()
            Repository.usuario?.token?.let{ token ->
                val response = asignarSoportesUserCase.invoke(token,idSoporte)
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

    fun cerrarSoporte(idSoporte: String){
        viewModelScope.launch {
            cerrarSoporteLiveData.value = Resource.loading()
            Repository.usuario?.token?.let{ token ->
                val response = cerrarSoporteUserCase.invoke(token,idSoporte)
                if (response != null) {
                    if (response.isOK()) {
                        cerrarSoporteLiveData.value = Resource.success(response)
                    } else if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                        cerrarSoporteLiveData.value = Resource.success(response)
                    } else {
                        cerrarSoporteLiveData.value = Resource.error(ErrorHelper.cerrarSoporteError)
                    }
                } else {
                    cerrarSoporteLiveData.value = Resource.error(ErrorHelper.cerrarSoporteError)
                }
            }
        }
    }

    fun sendRespuesta(idSoporte: String, respuesta: String){
        viewModelScope.launch {
            sendRespuestaLiveData.value = Resource.loading()
            Repository.usuario?.token?.let{ token ->
                val response = sendRespuestaUserCase.invoke(token,idSoporte, respuesta)
                if (response != null) {
                    if (response.isOK()) {
                        sendRespuestaLiveData.value = Resource.success(response)
                    } else if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                        sendRespuestaLiveData.value = Resource.success(response)
                    } else {
                        sendRespuestaLiveData.value = Resource.error(ErrorHelper.sendRespuestaError)
                    }
                } else {
                    sendRespuestaLiveData.value = Resource.error(ErrorHelper.sendRespuestaError)
                }
            }
        }
    }
}
