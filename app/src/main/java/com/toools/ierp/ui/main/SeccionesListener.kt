package com.toools.ierp.ui.main

import com.toools.ierp.data.ConstantHelper

interface SeccionesListener {
    fun selectedSeccion(seccion: ConstantHelper.Seccion)
    fun changeNotificaciones(isNotificacion: Boolean)
}