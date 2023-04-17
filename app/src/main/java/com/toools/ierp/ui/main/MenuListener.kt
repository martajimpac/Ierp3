package com.toools.ierp.ui.main

import com.toools.ierp.data.ConstantHelper

interface MenuListener {
    fun selectedSeccion(seccionMenu: ConstantHelper.SeccionMenu)
    fun changeNotificaciones(isNotificacion: Boolean)
}