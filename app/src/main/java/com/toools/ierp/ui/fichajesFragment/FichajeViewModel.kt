package com.toools.ierp.ui.fichajesFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.data.model.MomentosResponse
import com.toools.ierp.domain.EntradaSalidaUserCase
import com.toools.ierp.domain.MomentosDiaUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FichajeViewModel @Inject constructor(private val momentosDiaUserCase: MomentosDiaUserCase, private val entradaSalidaUserCase: EntradaSalidaUserCase): ViewModel() {

    val momentosDiaLiveData: MutableLiveData<Resource<MomentosResponse>> = MutableLiveData()
    val entradaSalidaLiveData: MutableLiveData<Resource<MomentosResponse>> = MutableLiveData()

    fun momentosDia(token: String, dia : Int, mes: Int, ano: Int){
        viewModelScope.launch {
            val response = momentosDiaUserCase.invoke(token, dia, mes, ano)
            if (response != null) {
                if (response.isOK()) {
                    momentosDiaLiveData.value = Resource.success(response)
                } else if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                    momentosDiaLiveData.value = Resource.success(response)
                }
            } else {
                momentosDiaLiveData.value = Resource.error(ErrorHelper.loginError)
            }
        }
    }

    fun entradaSalida(token: String, entrar: Int, latidud: Double, longitud: Double, comments: String, descripcion: String){
        viewModelScope.launch {
            val response = entradaSalidaUserCase.invoke(token, entrar, latidud, longitud, comments, descripcion)
            if (response != null) {
                if (response.isOK()) {
                    entradaSalidaLiveData.value = Resource.success(response)
                } else if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                    entradaSalidaLiveData.value = Resource.success(response)
                }
            } else {
                entradaSalidaLiveData.value = Resource.error(ErrorHelper.loginError)
            }
        }

    }
}