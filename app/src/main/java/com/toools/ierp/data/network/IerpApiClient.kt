package com.toools.ierp.data.network

import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.*
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface IerpApiClient {

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_LOGIN)
    fun loginIERP(@Field("client") client: String, @Field("usuario") usuario: String, @Field("password") password: String): Response<LoginResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_MOMENTOS)
    fun momentos(@Field("client") client: String, @Field("token") usuario: String): Response<MomentosResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_ENTRADAS_SALIDAS)
    fun entradasSalidas(@Field("client") client: String, @Field("token") usuario: String, @Field("dia") dia: Int, @Field("mes") mes: Int, @Field("ano") ano: Int): Response<MomentosResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_ADD_EVENT)
    fun entradaSalida(@Field("client") client: String, @Field("token") token: String, @Field("entradaSalida") entradaSalida: Int, @Field("latitud")
    latitud: Double, @Field("longitud") longitud: Double, @Field("comentario") comments: String, @Field("descripcion") descripcion: String): Response<MomentosResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_GUARDIAS)
    fun guardias(@Field("client") client: String, @Field("token") usuario: String, @Field("dia") dia: Int, @Field("mes") mes: Int, @Field("ano") ano: Int): Response<GuardiasResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_ADD_GUARDIA)
    fun addGuardia(@Field("client") client: String, @Field("token") usuario: String, @Field("momento") fecha: String,
                   @Field("descripcion") descripcion: String, @Field("tipo") tipo: Int): Response<GuardiasResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_PROYECTOS)
    fun proyectos(@Field("client") client: String, @Field("token") token: String): Response<ProyectosResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_PROYECTOS_SOPORTES)
    fun proyectosSoportes(@Field("client") client: String, @Field("token") token: String): Response<ProyectosResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_TAREAS)
    fun tareas(@Field("client") client: String, @Field("token") token: String): Response<TareasResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_ADD_TOKEN_FIREBASE)
    fun addTokenFirebase(@Field("client") client: String, @Field("token") token: String, @Field("tokenFirebase") tokenFirebase: String): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_CAMBIO_ESTADO)
    fun cambioEstadoTarea(@Field("client") client: String, @Field("token") token: String, @Field("idTarea") idTarea: String,
                          @Field("idEstado") idEstado: String, @Field("observacion") observacion: String): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_RESPUESTAS)
    fun respuestas(@Field("client") client: String, @Field("token") token: String, @Field("idIncidencia") idSoporte: String): Response<RespuestasResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_SOPORTES)
    fun soportes(@Field("client") client: String, @Field("token") token: String): Response<SoportesResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_SEND_RESPUESTA)
    fun sendRespuesta(@Field("client") client: String, @Field("token") token: String, @Field("idIncidencia") idSoporte: String, @Field("respuesta") respuesta: String): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_ASIGNAR_SOPORTE)
    fun asignarSoporte(@Field("client") client: String, @Field("token") token: String, @Field("idIncidencia") idSoporte: String): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_CERRAR_SOPORTE)
    fun cerrarSoporte(@Field("client") client: String, @Field("token") token: String, @Field("idIncidencia") idSoporte: String): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_INSERTAR_TAREA)
    fun insertarTarea(@Field("client") client: String, @Field("token") token: String, @Field("idProyecto") idProyecto: String, @Field("idEmpleado") idEmpleado: String,
                      @Field("titulo") titulo: String, @Field("descripcion") descripcion: String, @Field("plazo") plazo: String): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ConstantHelper.IERP_TAREAS_ASIGNADAS)
    fun tareasAsignadas(@Field("client") client: String, @Field("token") token: String): Response<TareasAsignadasResponse>

}