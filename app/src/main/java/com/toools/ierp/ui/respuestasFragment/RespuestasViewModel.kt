package com.toools.ierp.ui.respuestasFragment


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toools.ierp.core.Resource
import com.toools.ierp.data.model.BaseResponse
import com.toools.ierp.data.model.RespuestasResponse
import com.toools.ierp.domain.AsignarSoporteUserCase
import com.toools.ierp.domain.InsertarTareaUserCase
import com.toools.ierp.domain.ProyectosUserCase
import com.toools.ierp.domain.RespuestasUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RespuestasViewModel @Inject constructor(private val respuestasUserCase: RespuestasUserCase, private val asignarSoportesUserCase: AsignarSoporteUserCase): ViewModel() {

    val respuestasRecived: MutableLiveData<Resource<RespuestasResponse>> = MutableLiveData()
    val asignarSoportesRecived: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
    val cerrarSoportesRecived: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
    val nuevaRespuestaRecived: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun callRespuestas(idSoporte: String){

    }

    fun asignarSoportes(idSoporte: String){


    }

    fun cerrarSoportes(idSoporte: String){

        /*RestRepository.getInstance().usuario?.token?.let { token ->

            RestRepository.getInstance().cerrarSoporte(token, idSoporte, object:
                RestApiCallback<BaseResponse> {

                override fun onSuccess(response: BaseResponse) {
                    cerrarSoportesRecived.value = Resource.success(response)
                }

                override fun onFailure(throwable: Throwable) {
                    cerrarSoportesRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

                }
            })
        }*/
    }

    fun sendNuevaRespuesta(idSoporte: String, respuesta: String){

    }
}