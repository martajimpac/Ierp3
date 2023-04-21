package com.toools.ierp.ui.tareasFragment


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.data.Repository
import com.toools.ierp.data.model.BaseResponse
import com.toools.ierp.data.model.ProyectosResponse
import com.toools.ierp.data.model.TareasResponse
import com.toools.ierp.domain.CambioEstadoTareaUserCase
import com.toools.ierp.domain.ProyectosUserCase
import com.toools.ierp.domain.TareasUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TareasViewModel @Inject constructor(private val tareasUserCase: TareasUserCase, private val proyectosUserCase: ProyectosUserCase, private val cambioEstadoTareaUserCase: CambioEstadoTareaUserCase): ViewModel() {

    val tareasLiveData: MutableLiveData<Resource<TareasResponse>> = MutableLiveData()
    val proyectosLiveData: MutableLiveData<Resource<ProyectosResponse>> = MutableLiveData()
    val cambiarEstadoTareaLiveData: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
    val insertarTareaLiveData: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun tareas(){
        viewModelScope.launch {
            tareasLiveData.value = Resource.loading()
            Repository.usuario?.token?.let{ token ->
                val response = tareasUserCase.invoke(token)
                if (response != null) {
                    if (response.isOK()) {
                        tareasLiveData.value = Resource.success(response)
                    } else if (response.error != null && Integer.parseInt(response.error) == ErrorHelper.SESSION_EXPIRED) {
                        tareasLiveData.value = Resource.success(response)
                    } else {
                        tareasLiveData.value = Resource.error(ErrorHelper.tareasError)
                    }
                } else {
                    tareasLiveData.value = Resource.error(ErrorHelper.tareasError)
                }
            }
        }
    }

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

    fun cambiarEstadoTarea(idEstado: String, idTarea: String, observacion: String){
        
    }

    fun insertarTarea(idProyecto: String, idEmpleado: String, titulo: String, descripcion: String, plazo: String){
        
    }
}