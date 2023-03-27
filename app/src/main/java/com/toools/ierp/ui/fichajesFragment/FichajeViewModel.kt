package com.toools.ierp.ui.fichajesFragment

/*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.ierp.MomentosResponse
import com.toools.ierp.helpers.rest.AppException
import com.toools.ierp.helpers.rest.RestApiCallback
import com.toools.ierp.helpers.rest.RestRepository

class FichajeViewModel : ViewModel() {

    val momentosDiaRecived: MutableLiveData<Resource<MomentosResponse>> = MutableLiveData()

    fun callMomentosDia(token: String, dia : Int, mes: Int, anyo: Int){

        RestRepository.getInstance().momentosDia(token, dia, mes, anyo, object : RestApiCallback<MomentosResponse> {


            override fun onSuccess(response: MomentosResponse) {
                momentosDiaRecived.value = Resource.success(response)
            }

            override fun onFailure(throwable: Throwable) {
                momentosDiaRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

            }
        })

    }

    val addEventRecived: MutableLiveData<Resource<MomentosResponse>> = MutableLiveData()

    fun callAddEvent(token: String, entradaSalida: Int, latidud: Double, longitud: Double, comments: String, descripcion: String){

        RestRepository.getInstance().entradaSalida(token, entradaSalida, latidud, longitud, comments, descripcion, object : RestApiCallback<MomentosResponse> {


            override fun onSuccess(response: MomentosResponse) {
                addEventRecived.value = Resource.success(response)
            }

            override fun onFailure(throwable: Throwable) {
                addEventRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

            }
        })

    }
}*/