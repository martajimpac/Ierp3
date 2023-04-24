package com.toools.ierp.ui.proyectosFragment

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.core.prefs
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.Repository
import com.toools.ierp.data.model.BaseResponse
import com.toools.ierp.data.model.LoginResponse
import com.toools.ierp.data.model.ProyectosResponse
import com.toools.ierp.domain.EntradaSalidaUserCase
import com.toools.ierp.domain.InsertarTareaUserCase
import com.toools.ierp.domain.MomentosDiaUserCase
import com.toools.ierp.domain.ProyectosUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProyectosViewModel @Inject constructor(private val proyectosUserCase: ProyectosUserCase, private val insertarTareaUserCase: InsertarTareaUserCase): ViewModel() {

    val proyectosLiveData: MutableLiveData<Resource<ProyectosResponse>> = MutableLiveData()
    val insertarTareaLiveData: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun proyectos() {
        viewModelScope.launch {
            proyectosLiveData.value = Resource.loading()
            Repository.usuario?.token?.let{token ->
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

    fun insertarTarea(
        idProyecto: String,
        idEmpleado: String,
        titulo: String,
        descripcion: String,
        plazo: String
    ) {
        viewModelScope.launch {
            insertarTareaLiveData.value = Resource.loading()
            Repository.usuario?.token?.let { token ->
                val response = insertarTareaUserCase.invoke(token, idProyecto, idEmpleado, titulo, descripcion, plazo)

                if (response != null) {
                    if (response.isOK()) {
                        insertarTareaLiveData.value = Resource.success(response)
                    } else if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                        insertarTareaLiveData.value = Resource.success(response)
                    } else {
                        insertarTareaLiveData.value = Resource.error(ErrorHelper.insertarTareaError)
                    }
                } else {
                    insertarTareaLiveData.value = Resource.error(ErrorHelper.insertarTareaError)
                }
            }
        }
    }
}