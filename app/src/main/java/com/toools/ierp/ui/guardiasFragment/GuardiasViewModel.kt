package com.toools.ierp.ui.guardiasFragment

/*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.ierp.GuardiasResponse
import com.toools.ierp.helpers.rest.AppException
import com.toools.ierp.helpers.rest.RestApiCallback
import com.toools.ierp.helpers.rest.RestRepository
import java.util.*

class GuardiasViewModel: ViewModel() {

    val guardiasRecived: MutableLiveData<Resource<GuardiasResponse>> = MutableLiveData()

    fun callGuardias(token: String, dia : Int, mes: Int, anyo: Int){

        RestRepository.getInstance().guardias(token, dia, mes, anyo, object : RestApiCallback<GuardiasResponse> {


            override fun onSuccess(response: GuardiasResponse) {
                guardiasRecived.value = Resource.success(response)
            }

            override fun onFailure(throwable: Throwable) {
                guardiasRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

            }
        })

    }

    val addGuardiasRecived: MutableLiveData<Resource<GuardiasResponse>> = MutableLiveData()

    fun callAddGuardias(token: String, momento: Date, descripcion: String, tipo: Int){

        RestRepository.getInstance().addGuardia(token, momento, descripcion, tipo, object : RestApiCallback<GuardiasResponse> {


            override fun onSuccess(response: GuardiasResponse) {
                guardiasRecived.value = Resource.success(response)
            }

            override fun onFailure(throwable: Throwable) {
                guardiasRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

            }
        })

    }
}*/