package com.toools.ierp.ui.proyectosFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toools.ierp.core.Resource
import com.toools.ierp.data.model.BaseResponse
import com.toools.ierp.data.model.ProyectosResponse

class ProyectosViewModel : ViewModel() {

    val proyectosLiveData: MutableLiveData<Resource<ProyectosResponse>> = MutableLiveData()
    val insertarTareaLiveData: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun callProyectos() {

        //proyectos token conseguir de repo
    }

    fun insertarTarea(
        idProyecto: String,
        idEmpleado: String,
        titulo: String,
        descripcion: String,
        plazo: String
    ) {

       //insertartarea token de repo
    }
}