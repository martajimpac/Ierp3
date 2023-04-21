package com.toools.ierp.ui.tareasFragment

import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.Tarea


interface TareaListener {

    fun aceptarTarea(tarea: Tarea)
    fun cambiarEstadoTarea(tarea: Tarea, estado: ConstantHelper.Estados)
}