package com.toools.ierp.core

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.data.model.Usuario
import com.toools.ierp.databinding.RecyclerEmpleadoDialogBinding

class AdapterEmpleadosDialog (context: Context, listUsuarios: MutableList<Usuario>, itemClick: ((position: Int) -> Unit)) :
    RecyclerView.Adapter<AdapterEmpleadosDialog.EmpleadoHolder>() {

    private lateinit var binding : RecyclerEmpleadoDialogBinding

    private var listUsuarios: MutableList<Usuario> = mutableListOf()
    private var context: Context
    private var positionSelected = 0
    private var itemClick: ((position: Int) -> Unit)

    init {
        this.listUsuarios.addAll(listUsuarios)
        this.context = context
        this.itemClick = itemClick
    }

    fun setList(listUsuarios: MutableList<Usuario>?) {
        this.listUsuarios.clear()
        this.listUsuarios.addAll(listUsuarios!!)

        notifyDataSetChanged()
    }

    fun setPositionSelected(position: Int){

        val positionAnterior = positionSelected

        this.positionSelected = position
        notifyItemChanged(position)

        notifyItemChanged(positionAnterior)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpleadoHolder {
        binding = RecyclerEmpleadoDialogBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return EmpleadoHolder(binding)
    }

    override fun getItemCount(): Int {
        return listUsuarios.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: EmpleadoHolder, position: Int) {
        holder.bind(listUsuarios[position])

        if (position == positionSelected) {
            holder.borderUsuarioView.background = context.getDrawable(R.drawable.bg_circle_primary)
            holder.UsuarioTextView.setTextColor(context.getColor(R.color.colorPrimary))
        } else {
            holder.borderUsuarioView.background = context.getDrawable(R.drawable.circle_images_white_bg)
            holder.UsuarioTextView.setTextColor(context.getColor(R.color.black))
        }

        holder.UsuarioConstraintLayaot.setOnClickListener {
            itemClick(position)
        }
    }

    class EmpleadoHolder(val binding: RecyclerEmpleadoDialogBinding) : RecyclerView.ViewHolder(binding.root) {

        var borderUsuarioView = binding.bordeEmpleadoView
        var UsuarioTextView = binding.empleadoTextView
        var UsuarioConstraintLayaot = binding.empleadoConstraintLayaut

        fun bind(item: Usuario) = with(itemView) {
            binding.apply{
                Glide.with(context).load(item.imagen).circleCrop().error(
                    Glide.with(context).load(
                        R.drawable.luciano).circleCrop()).into(empleadoImageView)
                empleadoTextView.text = item.username
            }
        }
    }

}