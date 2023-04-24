package com.toools.ierp.ui.tareasAsignadasFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.TareaAsignada
import com.toools.ierp.databinding.RecyclerTareasBinding

class AdapterTareasAsignadas (context: Context, listTareas: MutableList<TareaAsignada>, var listener: ((tarea: TareaAsignada)-> Unit)) :
    RecyclerView.Adapter<AdapterTareasAsignadas.TareaHolder>() {

    private var listTareas: MutableList<TareaAsignada> = mutableListOf()
    private var context: Context
    private var holderSelec: TareaHolder? = null

    private lateinit var binding: RecyclerTareasBinding

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
        binding = RecyclerTareasBinding.inflate(LayoutInflater.from(parent.context),parent,false)
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

            tarea.estados?.let { estados ->
                if (estados.isNotEmpty())
                    estados.last().let {
                        if (it.idEstado == ConstantHelper.Estados.terminada.idEstado)
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

    class TareaHolder(val binding: RecyclerTareasBinding) : RecyclerView.ViewHolder(binding.root) {

        var headerConstraintLayout = binding.headerConstraintLayout
        var expandableLayout = binding.expandable
        var aceptarCardView = binding.aceptarTareaCardView

        fun bind(item: TareaAsignada) = with(itemView) {
            binding.apply {
                Glide.with(context).load(item.imgEmpleado)
                    .error(Glide.with(context).load(R.drawable.luciano).circleCrop()).circleCrop()
                    .into(empleadoImageView)
                emailTextView.text = item.nombreTarea
                descripcionTareaTextView.text = item.observaciones
                Glide.with(context).load(item.imgMiniProyecto)
                    .error(Glide.with(context).load(R.drawable.ic_proyectos).circleCrop())
                    .circleCrop().into(asignadoImageView)
                respuestaTextView.text = item.getPlazoFormateado()

                aceptarTareaTextView.text = context.getString(R.string.cerrar_tarea)
                aceptarTareaCardView.visibility = View.VISIBLE
                estadosConstraintLayout.visibility = View.GONE

                estadoView.setBackgroundColor(context.getColor(ConstantHelper.Estados.sinAbrir.color))
                estadoTextView.text = context.getString(ConstantHelper.Estados.sinAbrir.nombre)

                if (!item.estados.isNullOrEmpty()) {
                    when (item.estados.last().idEstado) {
                        ConstantHelper.Estados.abierta.idEstado -> {
                            estadoView.setBackgroundColor(context.getColor(ConstantHelper.Estados.abierta.color))
                            estadoTextView.text =
                                context.getString(ConstantHelper.Estados.abierta.nombre)
                        }
                        ConstantHelper.Estados.enProgreso.idEstado -> {
                            estadoView.setBackgroundColor(context.getColor(ConstantHelper.Estados.enProgreso.color))
                            estadoTextView.text =
                                context.getString(ConstantHelper.Estados.enProgreso.nombre)
                        }
                        ConstantHelper.Estados.enRevision.idEstado -> {
                            estadoView.setBackgroundColor(context.getColor(ConstantHelper.Estados.enRevision.color))
                            estadoTextView.text =
                                context.getString(ConstantHelper.Estados.enRevision.nombre)
                        }
                        ConstantHelper.Estados.terminada.idEstado -> {
                            estadoView.setBackgroundColor(context.getColor(ConstantHelper.Estados.terminada.color))
                            estadoTextView.text =
                                context.getString(ConstantHelper.Estados.terminada.nombre)
                        }
                    }
                }
            }
        }
    }
}