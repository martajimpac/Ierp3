package com.toools.ierp.ui.tareasFragment

/*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.entities.ierp.Tarea
import com.toools.ierp.helpers.ConstantsHelper
import kotlinx.android.synthetic.main.recycler_tareas.view.*

class AdapterTareas (context: Context, listTareas: MutableList<Tarea>, listener: TareaListener) :
    RecyclerView.Adapter<AdapterTareas.TareaHolder>() {

    private var listTareas: MutableList<Tarea> = mutableListOf()
    private var context: Context
    private var holderSelec: TareaHolder? = null
    private var listener: TareaListener

    init {
        this.listTareas.addAll(listTareas)
        this.context = context
        this.listener = listener
    }

    fun setList(listTareas: MutableList<Tarea>?) {
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

            holder.expandableLayout.toggle(true)

            if (holder.expandableLayout.isExpanded){
                holderSelec = holder
            }
        }

        holder.aceptarCardView.setOnClickListener {
            listener.aceptarTarea(tarea)
        }

        holder.abiertaCardView.setOnClickListener {
            listener.cambiarEstadoTarea(tarea, ConstantsHelper.Estados.abierta)
        }

        holder.enProgresoCardView.setOnClickListener {
            listener.cambiarEstadoTarea(tarea, ConstantsHelper.Estados.enProgreso)
        }

        holder.enRevisionCardView.setOnClickListener {
            listener.cambiarEstadoTarea(tarea, ConstantsHelper.Estados.enRevision)
        }

        holder.terminadaCardView.setOnClickListener {
            listener.cambiarEstadoTarea(tarea, ConstantsHelper.Estados.terminada)
        }

    }

    class TareaHolder(view: View) : RecyclerView.ViewHolder(view) {

        var headerConstraintLayout = view.headerConstraintLayout
        var expandableLayout = view.expandable
        var aceptarCardView = view.aceptarTareaCardView
        var abiertaCardView = view.abiertaCardView
        var enProgresoCardView = view.enProgresoCardView
        var enRevisionCardView = view.enRevisionCardView
        var terminadaCardView = view.terminadaCardView

        fun bind(item: Tarea) = with(itemView) {
            Glide.with(context).load(item.imgAutor).error(Glide.with(context).load(R.drawable.luciano).circleCrop()).circleCrop().into(empleadoImageView)
            emailTextView.text = item.nombreTarea
            descripcionTareaTextView.text = item.observaciones
            Glide.with(context).load(item.imgMiniProyecto).error(Glide.with(context).load(R.drawable.ic_proyectos).circleCrop()).circleCrop().into(asignadoImageView)
            respuestaTextView.text = item.getPlazoFormateado()

            aceptarTareaTextView.text = context.getString(R.string.aceptar_tarea)
            aceptarTareaCardView.visibility = if (item.isAceptada()) { View.GONE } else { View.VISIBLE }
            estadosConstraintLayout.visibility = if (item.isAceptada()) { View.VISIBLE } else { View.GONE }

            abiertaCardView.setCardBackgroundColor(context.getColor(R.color.color_default))
            enProgresoCardView.setCardBackgroundColor(context.getColor(R.color.color_default))
            enRevisionCardView.setCardBackgroundColor(context.getColor(R.color.color_default))
            terminadaCardView.setCardBackgroundColor(context.getColor(R.color.color_default))

            estadoView.setBackgroundColor(context.getColor(ConstantsHelper.Estados.sinAbrir.color))
            estadoTextView.text = context.getString(ConstantsHelper.Estados.sinAbrir.nombre)


            if (!item.estados.isNullOrEmpty()) {
                when (item.estados.last().idEstado) {
                    ConstantsHelper.Estados.abierta.idEstado -> {
                        abiertaCardView.setCardBackgroundColor(context.getColor(ConstantsHelper.Estados.abierta.color))
                        estadoView.setBackgroundColor(context.getColor(ConstantsHelper.Estados.abierta.color))
                        estadoTextView.text = context.getString(ConstantsHelper.Estados.abierta.nombre)
                    }
                    ConstantsHelper.Estados.enProgreso.idEstado -> {
                        enProgresoCardView.setCardBackgroundColor(context.getColor(ConstantsHelper.Estados.enProgreso.color))
                        estadoView.setBackgroundColor(context.getColor(ConstantsHelper.Estados.enProgreso.color))
                        estadoTextView.text = context.getString(ConstantsHelper.Estados.enProgreso.nombre)
                    }
                    ConstantsHelper.Estados.enRevision.idEstado -> {
                        enRevisionCardView.setCardBackgroundColor(context.getColor(ConstantsHelper.Estados.enRevision.color))
                        estadoView.setBackgroundColor(context.getColor(ConstantsHelper.Estados.enRevision.color))
                        estadoTextView.text = context.getString(ConstantsHelper.Estados.enRevision.nombre)
                    }
                    ConstantsHelper.Estados.terminada.idEstado -> {
                        terminadaCardView.setCardBackgroundColor(context.getColor(ConstantsHelper.Estados.terminada.color))
                        estadoView.setBackgroundColor(context.getColor(ConstantsHelper.Estados.terminada.color))
                        estadoTextView.text = context.getString(ConstantsHelper.Estados.terminada.nombre)
                    }
                }
            }

        }

    }

}*/