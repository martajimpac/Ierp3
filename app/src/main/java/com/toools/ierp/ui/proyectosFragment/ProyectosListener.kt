package com.toools.ierp.ui.proyectosFragment

import android.widget.ImageView
import com.toools.ierp.data.model.Proyecto

interface ProyectosListener {

    fun clickTareas(proyecto: Proyecto, miniaturaImageView: ImageView)
    fun clickSoportes(proyecto: Proyecto, miniaturaImageView: ImageView)
    fun clickEmpleados(proyecto: Proyecto)
    fun clickAddTarea(proyecto: Proyecto)
}