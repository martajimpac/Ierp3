package com.toools.ierp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R

/*
class AdapterProyectoGeneral (context: Context, listProyectos: MutableList<Proyecto>, itemClick: ((position: Int) -> Unit)) :
    RecyclerView.Adapter<AdapterProyectoGeneral.ProyectoHolder>() {

    private var listProyectos: MutableList<Proyecto> = mutableListOf()
    private var context: Context
    private var positionSelected = 0
    private var itemClick: ((position: Int) -> Unit)

    init {
        this.listProyectos.addAll(listProyectos)
        this.context = context
        this.itemClick = itemClick
    }

    fun setList(listProyectos: MutableList<Proyecto>?) {
        this.listProyectos.clear()
        this.listProyectos.addAll(listProyectos!!)

        notifyDataSetChanged()
    }

    fun setPositionSelected(position: Int){

        val positionAnterior = positionSelected

        this.positionSelected = position
        notifyItemChanged(position)

        notifyItemChanged(positionAnterior)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProyectoHolder {
        val inflater = LayoutInflater.from(this.context)
        val view = inflater.inflate(R.layout.recycler_proyecto_general, parent, false)
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

        if (position == positionSelected) {
            holder.borderProyectoView.background = context.getDrawable(R.drawable.bg_circle_primary)
            holder.proyectoTextView.setTextColor(context.getColor(R.color.colorPrimary))
        } else {
            holder.borderProyectoView.background = context.getDrawable(R.drawable.circle_images_white_bg)
            holder.proyectoTextView.setTextColor(context.getColor(R.color.black))
        }

        holder.proyectoConstraintLayaot.setOnClickListener {
            itemClick(position)
        }
    }

    class ProyectoHolder(view: View) : RecyclerView.ViewHolder(view) {

        var borderProyectoView = view.borderProyectoView
        var proyectoTextView = view.proyectoTextView
        var proyectoConstraintLayaot = view.proyectoConstraintLayaut

        fun bind(item: Proyecto) = with(itemView) {
            Glide.with(context).load(item.miniatura).circleCrop().error(Glide.with(context).load(R.drawable.ic_proyectos).circleCrop()).into(asignadoImageView)
            proyectoTextView.text = item.codigo

            ViewCompat.setTransitionName(asignadoImageView, context.getString(R.string.trans_proyecto, item.id))
        }

    }

}*/