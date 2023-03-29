package com.toools.ierp.data

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import com.toools.ierp.R
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

object ConstantHelper {

    //Rest constants
    const val iSpotBaseURL = "http://www.ispot.es/ws/"
    const val ierpBaseUrl = "http://www.ierp.es/ws/"

    const val pushIdREST = "id_push"
    const val os = "so"
    const val firebaseToken = "token_firebase"
    var clientREST: String = ""

    //ISPOT - addUser
    const val iSpotSystem = "android"
    const val iSpotClientId = "56"

    //SHAREDPREFERENCES
    const val iSpotBottomAdsImageUrl = "iSpotBottomAdsImageUrl"
    const val iSpotBottomAdsLinkUrl = "iSpotBottomAdsLinkUrl"
    const val iSpotTopAdsImageUrl = "iSpotTopAdsImageUrl"
    const val iSpotTopAdsLinkUrl = "iSpotTopAdsLinkUrl"
    const val iSpotMainAds = "iSpotMainAds"
    const val registeredFirebaseToken = "registeredFirebaseToken"
    const val pushId = "PUSH_ID"
    const val pushOS = "ANDROID"
    const val usuarioLogin = "usuario"
    const val autoLogin = "autoLogin"
    const val timeZone = "timeZone"

    const val addTokenFirebase = "addTokenFirebase"

    const val sendNotificacion = "sendNotificacion"
    const val horarioVerano = "horarioVerano"
    const val client = "client"
    const val centroTrabajo = "centroTrabajo"


    //name services
    const val IERP_LOGIN = "login"
    const val IERP_MOMENTOS = "momentos"
    const val IERP_ENTRADAS_SALIDAS = "entradasSalidas"
    const val IERP_ADD_EVENT = "entradaSalida"
    const val IERP_GUARDIAS = "guardias"
    const val IERP_ADD_GUARDIA = "insertarGuardia"
    const val IERP_PROYECTOS = "proyectos"
    const val IERP_PROYECTOS_SOPORTES = "proyectosSoporte"
    const val IERP_TAREAS = "tareas"
    const val IERP_ADD_TOKEN_FIREBASE = "addTokenFirebase"
    const val IERP_CAMBIO_ESTADO = "cambiarEstadoTarea"
    const val IERP_SOPORTES = "soportes"
    const val IERP_RESPUESTAS = "respuestas"
    const val IERP_SEND_RESPUESTA = "enviarRespuesta"
    const val IERP_ASIGNAR_SOPORTE = "asignarSoporte"
    const val IERP_CERRAR_SOPORTE = "cerrarSoporte"
    const val IERP_INSERTAR_TAREA = "insertarTarea"
    const val IERP_TAREAS_ASIGNADAS = "tareasAsignadas"

    const val firebaseProjectID = "balmy-gearing-92214"
    const val firebaseApplicationID = "1:943624393880:android:80e288528be67e58"
    const val firebaseApiKey = "AIzaSyCJJmQ-34NOO4IgCe0Q5rrEZk9kaJ8pP08"
    const val firebaseAppName = "IERP"

    //BROADCASTS
    const val broadCastFragmentIndexChange = "broadCastFragmentIndexChange"
    const val backToRoot = "backToRoot"

    //BUNDLE EXTRAS
    const val type = "type"

    fun urlEncode(s: String?): String {

        return if (s == null) {
            ""
        } else {

            try {

                URLEncoder.encode(s, "UTF8")
            } catch (var2: UnsupportedEncodingException) {

                throw RuntimeException(var2.message, var2)
            }
        }
    }


    fun getScreenShot(view: View): Bitmap {

        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    fun getWidhtScreen(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        //val height = displayMetrics.heightPixels
        return displayMetrics.widthPixels
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }


    enum class Seccion(var nombre: Int, var icono: Int) {

        fichajes(R.string.fichajes, R.drawable.ic_fichajes),
        guardias(R.string.guardias_seccion, R.drawable.ic_guardias),
        proyectos(R.string.proyectos, R.drawable.ic_proyectos),
        tareas(R.string.tareas, R.drawable.ic_tareas),
        tareasAsignadas(R.string.tareas_asignadas, R.drawable.ic_tareas_autor),
        soportes(R.string.soportes, R.drawable.ic_soportes),
        notificaciones(R.string.notificaciones, R.drawable.notifications_on)

    }

    enum class Estados(var idEstado: String, var color: Int, var nombre: Int) {

        abierta("1", R.color.color_abierta, R.string.abierta),
        enProgreso("2", R.color.color_en_proceso, R.string.en_progreso),
        enRevision("3", R.color.color_en_revision, R.string.en_revision),
        terminada("4", R.color.color_terminada, R.string.terminada),
        completada("5", R.color.color_default, R.string.completada),
        sinAbrir("0", R.color.color_sin_abierta, R.string.sin_abrir)

    }

    enum class Tipos(var idTipo: Int) {
        todos(0),
        propios(1),
        sinAbrir(2)
    }

}
