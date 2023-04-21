package com.toools.ierp.ui.proyectosFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.data.model.Proyecto
import com.toools.ierp.databinding.RecyclerProyectosBinding
import net.cachapa.expandablelayout.ExpandableLayout

class AdapterProyectos (context: Context, listProyectos: MutableList<Proyecto>, var listener: ProyectosListener) :
    RecyclerView.Adapter<AdapterProyectos.ProyectoHolder>() {


    private var listProyectos: MutableList<Proyecto> = mutableListOf()
    private var context: Context
    private var holderSelec: ProyectoHolder? = null

    private lateinit var binding: RecyclerProyectosBinding

    init {
        this.listProyectos.addAll(listProyectos)
        this.context = context
    }

    fun setList(listProyectos: MutableList<Proyecto>?) {
        this.listProyectos.clear()
        this.listProyectos.addAll(listProyectos!!)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProyectoHolder {
        binding = RecyclerProyectosBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProyectoHolder(binding)
    }

    override fun getItemCount(): Int {
        return listProyectos.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ProyectoHolder, position: Int) {
        holder.bind(listProyectos[position])

        val proyecto = listProyectos[position]

        holder.headerConstraintLayout.setOnClickListener {

            holderSelec?.let{
                if (it.absoluteAdapterPosition != holder.absoluteAdapterPosition && it.expandable.isExpanded)
                    it.expandable.toggle(true)
            }

            holder.expandable.toggle(true)

            if (holder.expandable.isExpanded){
                holderSelec = holder
            }

            holder.tareasView.setOnClickListener {
                listener.clickTareas(proyecto, holder.miniaturaImageView)
            }

            holder.soportesView.setOnClickListener {
                listener.clickSoportes(proyecto, holder.miniaturaImageView)
            }

            holder.empleadosView.setOnClickListener {
                listener.clickEmpleados(proyecto)
            }

            holder.tareasAsignadasView.setOnClickListener {
                listener.clickAddTarea(proyecto)
            }
        }
    }

    class ProyectoHolder(val binding: RecyclerProyectosBinding) : RecyclerView.ViewHolder(binding.root) {

        var headerConstraintLayout: ConstraintLayout = binding.headerConstraintLayout
        var expandable: ExpandableLayout = binding.expandable
        var tareasView: View = binding.tareasView
        var soportesView: View = binding.soportesView
        var empleadosView: View = binding.empleadosView
        var tareasAsignadasView: View = binding.tareasAsignadasView

        var miniaturaImageView = binding.miniaturaImageView

        fun bind(item: Proyecto) = with(itemView) {
            binding.apply{
                Glide.with(context).load(item.imagen).into(asignadoImageView)
                proyectoTextView.text = item.nombre
                Glide.with(context).load(item.miniatura).circleCrop().error(Glide.with(context).load(R.drawable.ic_proyectos)).into(miniaturaImageView)
            }
            //animacion
            ViewCompat.setTransitionName(miniaturaImageView, context.getString(R.string.trans_proyecto, item.id))
        }
    }
}