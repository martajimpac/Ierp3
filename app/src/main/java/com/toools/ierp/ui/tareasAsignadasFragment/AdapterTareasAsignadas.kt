package com.toools.ierp.ui.tareasAsignadasFragment

/*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.entities.ierp.TareaAsignada
import com.toools.ierp.helpers.ConstantsHelper
import kotlinx.android.synthetic.main.recycler_tareas.view.*

class AdapterTareasAsignadas (context: Context, listTareas: MutableList<TareaAsignada>, var listener: ((tarea: TareaAsignada)-> Unit)) :
    RecyclerView.Adapter<AdapterTareasAsignadas.TareaHolder>() {

    private var listTareas: MutableList<TareaAsignada> = mutableListOf()
    private var context: Context
    private var holderSelec: TareaHolder? = null

    init {
        this.listTareas.addAll(listTareas)
        this.context = context
    }

    fun setList(listTareas: MutableList<TareaAsignada>?) {
        this.listTareas.clear()
        this.listTareas.addAll(listTareas!!)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaHolder {
        val inflater = LayoutInflater.from(this.context)
        val view = inflater.inflate(R.layout.recycler_tareas, parent, false)
        return TareaHolder(view)
    }

    override fun getItemCount(): Int {
        return listTareas.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: TareaHolder, position: Int) {
        holder.bind(listTareas[position])

        val tarea = listTareas[position]

        holder.headerConstraintLayout.setOnClickListener {

            holderSelec?.let{
                if (it.absoluteAdapterPosition != holder.absoluteAdapterPosition && it.expandableLayout.isExpanded)
                    it.expandableLayout.toggle(true)
            }

            tarea.estados?.let { estados ->
                if (estados.isNotEmpty())
                    estados.last().let {
                        if (it.idEstado == ConstantsHelper.Estados.terminada.idEstado)
                            holder.expandableLayout.toggle(true)
                    }
            }

            if (holder.expandableLayout.isExpanded){
                holderSelec = holder
            }
        }

        holder.aceptarCardView.setOnClickListener {
            listener(tarea)
        }
    }

    class TareaHolder(view: View) : RecyclerView.ViewHolder(view) {

        var headerConstraintLayout = view.headerConstraintLayout
        var expandableLayout = view.expandable
        var aceptarCardView = view.aceptarTareaCardView

        fun bind(item: TareaAsignada) = with(itemView) {

            Glide.with(context).load(item.imgEmpleado).error(Glide.with(context).load(R.drawable.luciano).circleCrop()).circleCrop().into(empleadoImageView)
            emailTextView.text = item.nombreTarea
            descripcionTareaTextView.text = item.observaciones
            Glide.with(context).load(item.imgMiniProyecto).error(Glide.with(context).load(R.drawable.ic_proyectos).circleCrop()).circleCrop().into(asignadoImageView)
            respuestaTextView.text = item.getPlazoFormateado()

            aceptarTareaTextView.text = context.getString(R.string.cerrar_tarea)
            aceptarTareaCardView.visibility = View.VISIBLE
            estadosConstraintLayout.visibility = View.GONE

            estadoView.setBackgroundColor(context.getColor(ConstantsHelper.Estados.sinAbrir.color))
            estadoTextView.text = context.getString(ConstantsHelper.Estados.sinAbrir.nombre)

            if (!item.estados.isNullOrEmpty()) {
                when (item.estados.last().idEstado) {
                    ConstantsHelper.Estados.abierta.idEstado -> {
                        estadoView.setBackgroundColor(context.getColor(ConstantsHelper.Estados.abierta.color))
                        estadoTextView.text = context.getString(ConstantsHelper.Estados.abierta.nombre)
                    }
                    ConstantsHelper.Estados.enProgreso.idEstado -> {
                        estadoView.setBackgroundColor(context.getColor(ConstantsHelper.Estados.enProgreso.color))
                        estadoTextView.text = context.getString(ConstantsHelper.Estados.enProgreso.nombre)
                    }
                    ConstantsHelper.Estados.enRevision.idEstado -> {
                        estadoView.setBackgroundColor(context.getColor(ConstantsHelper.Estados.enRevision.color))
                        estadoTextView.text = context.getString(ConstantsHelper.Estados.enRevision.nombre)
                    }
                    ConstantsHelper.Estados.terminada.idEstado -> {
                        estadoView.setBackgroundColor(context.getColor(ConstantsHelper.Estados.terminada.color))
                        estadoTextView.text = context.getString(ConstantsHelper.Estados.terminada.nombre)
                    }
                }
            }

        }

    }

}*/