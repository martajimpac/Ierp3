package com.toools.ierp.ui.respuestasFragment

/*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.RestBaseObject
import com.toools.ierp.entities.ierp.RespuestasResponse
import com.toools.ierp.helpers.rest.AppException
import com.toools.ierp.helpers.rest.RestApiCallback
import com.toools.ierp.helpers.rest.RestRepository

class RespuestasViewModel: ViewModel() {

    val respuestasRecived: MutableLiveData<Resource<RespuestasResponse>> = MutableLiveData()
    val asignarSoportesRecived: MutableLiveData<Resource<RestBaseObject>> = MutableLiveData()
    val cerrarSoportesRecived: MutableLiveData<Resource<RestBaseObject>> = MutableLiveData()
    val nuevaRespuestaRecived: MutableLiveData<Resource<RestBaseObject>> = MutableLiveData()

    fun callRespuestas(idSoporte: String){

        RestRepository.getInstance().usuario?.token?.let { token ->

            RestRepository.getInstance().respuestas(token, idSoporte, object:
                RestApiCallback<RespuestasResponse> {

                override fun onSuccess(response: RespuestasResponse) {
                    respuestasRecived.value = Resource.success(response)
                }

                override fun onFailure(throwable: Throwable) {
                    respuestasRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

                }
            })
        }
    }

    fun asignarSoportes(idSoporte: String){

        RestRepository.getInstance().usuario?.token?.let { token ->

            RestRepository.getInstance().asignarSoporte(token, idSoporte, object:
                RestApiCallback<RestBaseObject> {

                override fun onSuccess(response: RestBaseObject) {
                    asignarSoportesRecived.value = Resource.success(response)
                }

                override fun onFailure(throwable: Throwable) {
                    asignarSoportesRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

                }
            })
        }
    }

    fun cerrarSoportes(idSoporte: String){

        RestRepository.getInstance().usuario?.token?.let { token ->

            RestRepository.getInstance().cerrarSoporte(token, idSoporte, object:
                RestApiCallback<RestBaseObject> {

                override fun onSuccess(response: RestBaseObject) {
                    cerrarSoportesRecived.value = Resource.success(response)
                }

                override fun onFailure(throwable: Throwable) {
                    cerrarSoportesRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

                }
            })
        }
    }

    fun sendNuevaRespuesta(idSoporte: String, respuesta: String){

        RestRepository.getInstance().usuario?.token?.let { token ->

            RestRepository.getInstance().sendRespuesta(token, idSoporte, respuesta, object:
                RestApiCallback<RestBaseObject> {

                override fun onSuccess(response: RestBaseObject) {
                    nuevaRespuestaRecived.value = Resource.success(response)
                }

                override fun onFailure(throwable: Throwable) {
                    nuevaRespuestaRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

                }
            })
        }
    }
}*/