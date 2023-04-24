package com.toools.ierp.data

import com.toools.ierp.data.model.*
import com.toools.ierp.data.network.IerpService
import com.toools.ierp.data.network.IspotService
import javax.inject.Inject

class Repository @Inject constructor(private val ispotService: IspotService, private val ierpService: IerpService) {

    //IERP
    suspend fun login(client: String, usuario: String, password: String): LoginResponse? {
        return ierpService.login(client, usuario, password)
    }

    suspend fun addTokenFirebase(token: String, tokenFirebase: String): BaseResponse? {
        return ierpService.addTokenFirebase(token,tokenFirebase)
    }

    suspend fun momentos(usuario: String): MomentosResponse? {
        return ierpService.momentos(usuario)
    }

    suspend fun momentosDia(usuario: String,dia: Int, mes: Int, ano: Int): MomentosResponse? {
        return ierpService.momentosDia(usuario,dia,mes,ano)
    }

    suspend fun entradaSalida(token: String,entrar: Int, latitud: Double, longitud:Double,comments:String,descripcion:String): MomentosResponse? {
        return ierpService.entradaSalida(token,entrar,latitud,longitud,comments,descripcion)
    }

    suspend fun guardias(usuario: String,dia: Int, mes: Int, ano: Int): GuardiasResponse? {
        return ierpService.guardias(usuario,dia,mes,ano)
    }

    suspend fun addGuardia(usuario: String,fecha:String,descripcion: String,tipo:Int): GuardiasResponse? {
        return ierpService.addGuardia(usuario,fecha,descripcion,tipo)
    }

    suspend fun proyectos(token: String): ProyectosResponse? {
        return ierpService.proyectos(token)
    }

    suspend fun insertarTarea(token: String,idProyecto: String, idEmpleado: String, titulo:String,descripcion: String, plazo: String): BaseResponse? {
        return ierpService.insertarTarea(token,idProyecto,idEmpleado,titulo,descripcion,plazo)
    }

    suspend fun cerrarSoporte(token: String,idSoporte: String): BaseResponse? {
        return ierpService.cerrarSoporte(token,idSoporte)
    }

    suspend fun respuestas(token: String,idSoporte: String): RespuestasResponse? {
        return ierpService.respuestas(token,idSoporte)
    }

    suspend fun sendRespuesta(token: String,idSoporte: String,respuesta:String): BaseResponse? {
        return ierpService.sendRespuesta(token,idSoporte,respuesta)
    }

    suspend fun proyectosSoportes(token: String): BaseResponse? {
        return ierpService.proyectosSoportes(token)
    }

    suspend fun soportes(token: String): SoportesResponse? {
        return ierpService.soportes(token)
    }

    suspend fun asignarSoporte(token: String,idSoporte: String): BaseResponse? {
        return ierpService.asignarSoporte(token,idSoporte)
    }

    suspend fun tareas(token: String): TareasResponse? {
        return ierpService.tareas(token)
    }

    suspend fun cambioEstadoTarea(token: String,idTarea: String,idEstado:String,observacion:String): BaseResponse? {
        return ierpService.cambioEstadoTarea(token,idTarea,idEstado,observacion)
    }

    suspend fun tareasAsignadas(token: String): TareasAsignadasResponse? {
        return ierpService.tareasAsignadas(token)
    }

    //ISPOT
    suspend fun addUser(tokenFirebase: String): BaseResponse? {
        return ispotService.addUser(tokenFirebase)
    }

    suspend fun clientData(clientID: String): ISpotClientDataResponse? {
        return ispotService.clientData(clientID)
    }

    suspend fun directAds(clientID: String): ISpotDirectAdsResponse? {
        return ispotService.directAds(clientID)
    }

    suspend fun notifications(clientID: String): ISpotNotificationsResponse? {
        return ispotService.notifications(clientID)
    }

    companion object{
        var usuario: LoginResponse?=null
    }

}