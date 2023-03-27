package com.toools.ierp.ui.soportesFragment

/*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.RestBaseObject
import com.toools.ierp.entities.ierp.ProyectosResponse
import com.toools.ierp.entities.ierp.SoportesResponse
import com.toools.ierp.helpers.rest.AppException
import com.toools.ierp.helpers.rest.RestApiCallback
import com.toools.ierp.helpers.rest.RestRepository

class SoportesViewModel: ViewModel() {

    val proyectosRecived: MutableLiveData<Resource<ProyectosResponse>> = MutableLiveData()
    val soportesRecived: MutableLiveData<Resource<SoportesResponse>> = MutableLiveData()
    val asignarSoportesRecived: MutableLiveData<Resource<RestBaseObject>> = MutableLiveData()

    fun callProyectos(){

        RestRepository.getInstance().usuario?.token?.let { token ->

            RestRepository.getInstance().proyectosSoportes(token, object:
                RestApiCallback<ProyectosResponse> {

                override fun onSuccess(response: ProyectosResponse) {
                    proyectosRecived.value = Resource.success(response)
                }

                override fun onFailure(throwable: Throwable) {
                    proyectosRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

                }
            })
        }
    }

    fun callSoportes(){

        RestRepository.getInstance().usuario?.token?.let { token ->

            RestRepository.getInstance().soportes(token, object:
                RestApiCallback<SoportesResponse> {

                override fun onSuccess(response: SoportesResponse) {
                    soportesRecived.value = Resource.success(response)
                }

                override fun onFailure(throwable: Throwable) {
                    soportesRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

                }
            })
        }
    }

    fun callAsignarSoportes(idSoporte: String){

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
}*/