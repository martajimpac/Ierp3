package com.toools.ierp.ui.soportesFragment

import com.toools.ierp.data.model.Soporte


interface SoportesListener {

    fun clickAsignarSoporte(soporte: Soporte)
    fun clickSoporte(soporte: Soporte)
}