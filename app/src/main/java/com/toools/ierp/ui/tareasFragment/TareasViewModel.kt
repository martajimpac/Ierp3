package com.toools.ierp.ui.tareasFragment

/*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.RestBaseObject
import com.toools.ierp.entities.ierp.ProyectosResponse
import com.toools.ierp.entities.ierp.TareasResponse
import com.toools.ierp.helpers.rest.AppException
import com.toools.ierp.helpers.rest.RestApiCallback
import com.toools.ierp.helpers.rest.RestRepository

class TareasViewModel : ViewModel() {

    val tareasRecived: MutableLiveData<Resource<TareasResponse>> = MutableLiveData()
    val proyectosRecived: MutableLiveData<Resource<ProyectosResponse>> = MutableLiveData()
    val cambioTareaRecived: MutableLiveData<Resource<RestBaseObject>> = MutableLiveData()
    val addTareaRecived: MutableLiveData<Resource<RestBaseObject>> = MutableLiveData()

    fun callTareas(){

        RestRepository.getInstance().usuario?.token?.let { token ->

            RestRepository.getInstance().tareas(token, object: RestApiCallback<TareasResponse> {

                override fun onSuccess(response: TareasResponse) {
                    tareasRecived.value = Resource.success(response)
                }

                override fun onFailure(throwable: Throwable) {
                    tareasRecived.value =
                        Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

                }
            })
        }
    }

    fun callProyectos(){

        RestRepository.getInstance().usuario?.token?.let { token ->

            RestRepository.getInstance().proyectos(token, object: RestApiCallback<ProyectosResponse> {

                override fun onSuccess(response: ProyectosResponse) {
                    proyectosRecived.value = Resource.success(response)
                }

                override fun onFailure(throwable: Throwable) {
                    proyectosRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

                }
            })
        }
    }

    fun cambiarEstadoTarea(idEstado: String, idTarea: String, observacion: String){

        RestRepository.getInstance().usuario?.token?.let { token ->

            RestRepository.getInstance().cambioEstadoTarea(token, idTarea, idEstado, observacion, object: RestApiCallback<RestBaseObject> {

                override fun onSuccess(response: RestBaseObject) {
                    cambioTareaRecived.value = Resource.success(response)
                }

                override fun onFailure(throwable: Throwable) {
                    cambioTareaRecived.value = Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

                }
            })
        }
    }

    fun addTarea(idProyecto: String, idEmpleado: String, titulo: String, descripcion: String, plazo: String){

        RestRepository.getInstance().usuario?.token?.let { token ->

            RestRepository.getInstance().insertarTarea(token, idProyecto, idEmpleado, titulo, descripcion, plazo, object : RestApiCallback<RestBaseObject> {

                override fun onSuccess(response: RestBaseObject) {
                    addTareaRecived.value = Resource.success(response)
                }

                override fun onFailure(throwable: Throwable) {
                    addTareaRecived.value =
                        Resource.error(AppException(string = if (throwable.localizedMessage.isNullOrEmpty()) throwable.toString() else throwable.localizedMessage))

                }
            })
        }
    }
}*/