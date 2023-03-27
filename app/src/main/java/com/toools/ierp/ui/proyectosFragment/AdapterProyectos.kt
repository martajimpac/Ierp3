package com.toools.ierp.ui.proyectosFragment

/*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.entities.ierp.Proyecto
import kotlinx.android.synthetic.main.recycler_proyectos.view.*
import net.cachapa.expandablelayout.ExpandableLayout

class AdapterProyectos (context: Context, listProyectos: MutableList<Proyecto>, var listener: ProyectosListener) :
    RecyclerView.Adapter<AdapterProyectos.ProyectoHolder>() {


    private var listProyectos: MutableList<Proyecto> = mutableListOf()
    private var context: Context
    private var holderSelec: ProyectoHolder? = null

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
        val inflater = LayoutInflater.from(this.context)
        val view = inflater.inflate(R.layout.recycler_proyectos, parent, false)
        return ProyectoHolder(view)
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

    class ProyectoHolder(view: View) : RecyclerView.ViewHolder(view) {

        var expandable: ExpandableLayout = view.expandable
        var headerConstraintLayout: ConstraintLayout = view.headerConstraintLayout
        var tareasView: View = view.tareasView
        var soportesView: View = view.soportesView
        var empleadosView: View = view.empleadosView
        var tareasAsignadasView: View = view.tareasAsignadasView
        var miniaturaImageView = view.miniaturaImageView

        fun bind(item: Proyecto) = with(itemView) {
            Glide.with(context).load(item.imagen).into(asignadoImageView)
            proyectoTextView.text = item.nombre
            Glide.with(context).load(item.miniatura).circleCrop().error(Glide.with(context).load(R.drawable.ic_proyectos)).into(miniaturaImageView)

            ViewCompat.setTransitionName(miniaturaImageView, context.getString(R.string.trans_proyecto, item.id))
        }
    }
}*/