package com.toools.ierp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.data.model.Proyecto
import com.toools.ierp.databinding.RecyclerProyectoGeneralBinding


class AdapterProyectoGeneral (context: Context, listProyectos: MutableList<Proyecto>, itemClick: ((position: Int) -> Unit)) :
    RecyclerView.Adapter<AdapterProyectoGeneral.ProyectoGeneralHolder>() {

    private var listProyectos: MutableList<Proyecto> = mutableListOf()
    private var context: Context
    private var positionSelected = 0
    private var itemClick: ((position: Int) -> Unit)

    private lateinit var binding: RecyclerProyectoGeneralBinding

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProyectoGeneralHolder {
        binding = RecyclerProyectoGeneralBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProyectoGeneralHolder(binding)
    }

    override fun getItemCount(): Int {
        return listProyectos.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ProyectoGeneralHolder, position: Int) {
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

    class ProyectoGeneralHolder(val binding: RecyclerProyectoGeneralBinding) : RecyclerView.ViewHolder(binding.root) {

        var borderProyectoView = binding.borderProyectoView
        var proyectoTextView = binding.proyectoTextView
        var proyectoConstraintLayaot = binding.proyectoConstraintLayaut

        fun bind(item: Proyecto) = with(itemView) {
            Glide.with(context).load(item.miniatura).circleCrop().error(Glide.with(context).load(R.drawable.ic_proyectos).circleCrop()).into(binding.asignadoImageView)
            proyectoTextView.text = item.codigo

            ViewCompat.setTransitionName(binding.asignadoImageView, context.getString(R.string.trans_proyecto, item.id))
        }

    }

}