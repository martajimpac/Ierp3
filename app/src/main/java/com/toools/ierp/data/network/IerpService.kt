package com.toools.ierp.data.network

import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IerpService @Inject constructor(private val api: IerpApiClient) {

    suspend fun login(client: String, usuario: String, password: String): LoginResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.loginIERP(client,usuario,password)
            response.body()
        }
    }

    suspend fun addTokenFirebase(token: String, tokenFirebase: String): BaseResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.addTokenFirebase(ConstantHelper.clientREST,token,tokenFirebase)
            response.body()
        }
    }

    suspend fun momentos(usuario: String): MomentosResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.momentos(ConstantHelper.clientREST,usuario)
            response.body()
        }
    }

    suspend fun momentosDia(usuario: String,dia: Int, mes: Int, ano: Int): MomentosResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.momentosDia(ConstantHelper.clientREST,usuario,dia,mes,ano)
            response.body()
        }
    }

    suspend fun entradaSalida(token: String,entradaSalida: Int, latitud: Double, longitud:Double,comments:String,descripcion:String): MomentosResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.entradaSalida(ConstantHelper.clientREST,token,entradaSalida,latitud,longitud,comments,descripcion)
            response.body()
        }
    }

    suspend fun guardias(usuario: String,dia: Int, mes: Int, ano: Int): GuardiasResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.guardias(ConstantHelper.clientREST,usuario,dia,mes,ano)
            response.body()
        }
    }

    suspend fun addGuardia(usuario: String,fecha:String,descripcion: String,tipo:Int): GuardiasResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.addGuardia(ConstantHelper.clientREST,usuario,fecha,descripcion,tipo)
            response.body()
        }
    }

    suspend fun proyectos(token: String): ProyectosResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.proyectos(ConstantHelper.clientREST,token)
            response.body()
        }
    }
    suspend fun insertarTarea(token: String,idProyecto: String, idEmpleado: String, titulo:String,descripcion: String, plazo: String): BaseResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.insertarTarea(ConstantHelper.clientREST,token,idProyecto,idEmpleado,titulo,descripcion,plazo)
            response.body()
        }
    }

    suspend fun cerrarSoporte(token: String,idSoporte: String): BaseResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.cerrarSoporte(ConstantHelper.clientREST,token,idSoporte)
            response.body()
        }
    }

    suspend fun respuestas(token: String,idSoporte: String): RespuestasResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.respuestas(ConstantHelper.clientREST,token,idSoporte)
            response.body()
        }
    }

    suspend fun sendRespuesta(token: String,idSoporte: String,respuesta:String): BaseResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.sendRespuesta(ConstantHelper.clientREST,token,idSoporte,respuesta)
            response.body()
        }
    }

    suspend fun proyectosSoportes(token: String): ProyectosResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.proyectosSoportes(ConstantHelper.clientREST,token)
            response.body()
        }
    }

    suspend fun soportes(token: String): SoportesResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.soportes(ConstantHelper.clientREST,token)
            response.body()
        }
    }

    suspend fun asignarSoporte(token: String,idSoporte: String): BaseResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.asignarSoporte(ConstantHelper.clientREST,token,idSoporte)
            response.body()
        }
    }

    suspend fun tareas(token: String): TareasResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.tareas(ConstantHelper.clientREST,token)
            response.body()
        }
    }

    suspend fun cambioEstadoTarea(token: String,idTarea: String,idEstado:String,observacion:String): BaseResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.cambioEstadoTarea(ConstantHelper.clientREST,token,idTarea,idEstado,observacion)
            response.body()
        }
    }

    suspend fun tareasAsignadas(token: String): TareasAsignadasResponse? {
        return withContext(Dispatchers.IO) {
            val response = api.tareasAsignadas(ConstantHelper.clientREST,token)
            response.body()
        }
    }
}