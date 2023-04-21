package com.toools.ierp.ui.tareasFragment


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.Tarea
import com.toools.ierp.databinding.RecyclerTareasBinding
import com.toools.ierp.ui.main.AdapterMenu


class AdapterTareas (context: Context, listTareas: MutableList<Tarea>, listener: TareaListener) :
    RecyclerView.Adapter<AdapterTareas.TareaHolder>() {

    private var listTareas: MutableList<Tarea> = mutableListOf()
    private var context: Context
    private var holderSelec: TareaHolder? = null
    private var listener: TareaListener

    private lateinit var binding: RecyclerTareasBinding

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
        binding = RecyclerTareasBinding.inflate(LayoutInflater.from(parent.context),parent,false) //esto creo que es asi pero no se porque
        return TareaHolder(binding)
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
            listener.cambiarEstadoTarea(tarea, ConstantHelper.Estados.abierta)
        }

        holder.enProgresoCardView.setOnClickListener {
            listener.cambiarEstadoTarea(tarea, ConstantHelper.Estados.enProgreso)
        }

        holder.enRevisionCardView.setOnClickListener {
            listener.cambiarEstadoTarea(tarea, ConstantHelper.Estados.enRevision)
        }

        holder.terminadaCardView.setOnClickListener {
            listener.cambiarEstadoTarea(tarea, ConstantHelper.Estados.terminada)
        }
    }

    class TareaHolder(val binding: RecyclerTareasBinding) : RecyclerView.ViewHolder(binding.root) {

        var headerConstraintLayout = binding.headerConstraintLayout
        var expandableLayout = binding.expandable
        var aceptarCardView = binding.aceptarTareaCardView
        var abiertaCardView = binding.abiertaCardView
        var enProgresoCardView = binding.enProgresoCardView
        var enRevisionCardView = binding.enRevisionCardView
        var terminadaCardView = binding.terminadaCardView

        fun bind(item: Tarea) = with(itemView) {
            binding.apply {
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

                estadoView.setBackgroundColor(context.getColor(ConstantHelper.Estados.sinAbrir.color))
                estadoTextView.text = context.getString(ConstantHelper.Estados.sinAbrir.nombre)


                if (!item.estados.isNullOrEmpty()) {
                    when (item.estados.last().idEstado) {
                        ConstantHelper.Estados.abierta.idEstado -> {
                            abiertaCardView.setCardBackgroundColor(context.getColor(ConstantHelper.Estados.abierta.color))
                            estadoView.setBackgroundColor(context.getColor(ConstantHelper.Estados.abierta.color))
                            estadoTextView.text = context.getString(ConstantHelper.Estados.abierta.nombre)
                        }
                        ConstantHelper.Estados.enProgreso.idEstado -> {
                            enProgresoCardView.setCardBackgroundColor(context.getColor(ConstantHelper.Estados.enProgreso.color))
                            estadoView.setBackgroundColor(context.getColor(ConstantHelper.Estados.enProgreso.color))
                            estadoTextView.text = context.getString(ConstantHelper.Estados.enProgreso.nombre)
                        }
                        ConstantHelper.Estados.enRevision.idEstado -> {
                            enRevisionCardView.setCardBackgroundColor(context.getColor(ConstantHelper.Estados.enRevision.color))
                            estadoView.setBackgroundColor(context.getColor(ConstantHelper.Estados.enRevision.color))
                            estadoTextView.text = context.getString(ConstantHelper.Estados.enRevision.nombre)
                        }
                        ConstantHelper.Estados.terminada.idEstado -> {
                            terminadaCardView.setCardBackgroundColor(context.getColor(ConstantHelper.Estados.terminada.color))
                            estadoView.setBackgroundColor(context.getColor(ConstantHelper.Estados.terminada.color))
                            estadoTextView.text = context.getString(ConstantHelper.Estados.terminada.nombre)
                        }
                    }
                }
            }
        }
    }
}