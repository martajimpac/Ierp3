package com.toools.ierp.ui.guardiasFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.data.model.GuardiasResponse
import com.toools.ierp.domain.AddGuardiaUserCase
import com.toools.ierp.domain.GuardiasUserCase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GuardiasViewModel @Inject constructor(private val guardiasUserCase: GuardiasUserCase,private val  addGuardiaUserCase: AddGuardiaUserCase): ViewModel() {

    val guardiasLiveData: MutableLiveData<Resource<GuardiasResponse>> = MutableLiveData()
    val addGuardiasLiveData: MutableLiveData<Resource<GuardiasResponse>> = MutableLiveData()

    fun guardias(token: String, dia : Int, mes: Int, anyo: Int){
        viewModelScope.launch {
            guardiasLiveData.value = Resource.loading()
            val response = guardiasUserCase.invoke(token, dia, mes, anyo)
            if (response != null) {
                if (response.isOK()) {
                    guardiasLiveData.value = Resource.success(response)
                } else if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                    guardiasLiveData.value = Resource.success(response)
                } else {
                    guardiasLiveData.value = Resource.error(ErrorHelper.guardiasError)
                }
            } else {
                guardiasLiveData.value = Resource.error(ErrorHelper.guardiasError)
            }
            
        }
    }

    fun addGuardias(token: String, momento: Date, descripcion: String, tipo: Int){
        viewModelScope.launch {
            addGuardiasLiveData.value = Resource.loading()
            //pasar momento a string
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val response = addGuardiaUserCase.invoke(token, format.format(momento), descripcion, tipo)
            if (response != null) {
                if (response.isOK()) {
                    addGuardiasLiveData.value = Resource.success(response)
                } else if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                    addGuardiasLiveData.value = Resource.success(response)
                } else {
                    addGuardiasLiveData.value = Resource.error(ErrorHelper.addGuardiaError)
                }
            } else {
                addGuardiasLiveData.value = Resource.error(ErrorHelper.addGuardiaError)
            }

        }
    }
}