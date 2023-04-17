package com.toools.ierp.ui.proyectosFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.data.model.Usuario
import com.toools.ierp.databinding.RecyclerEmpleadoBinding
import com.toools.ierp.databinding.RecyclerProyectosBinding


class AdapterEmpleados (context: Context, listEmpleados: MutableList<Usuario>) :
    RecyclerView.Adapter<AdapterEmpleados.UsuarioHolder>() {


    private var listEmpleados: MutableList<Usuario> = mutableListOf()
    private var context: Context
    private lateinit var binding: RecyclerEmpleadoBinding

    init {
        this.listEmpleados.addAll(listEmpleados)
        this.context = context
    }

    fun setList(listEmpleados: MutableList<Usuario>?) {
        this.listEmpleados.clear()
        this.listEmpleados.addAll(listEmpleados!!)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioHolder {
        binding = RecyclerEmpleadoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UsuarioHolder(binding)
    }

    override fun getItemCount(): Int {
        return listEmpleados.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: UsuarioHolder, position: Int) {
        holder.bind(listEmpleados[position])
    }

    class UsuarioHolder(val binding: RecyclerEmpleadoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Usuario) = with(itemView) {
            Glide.with(context).load(item.imagen).circleCrop().into(binding.empleadoImageView)
            binding.nombreTextView.text = item.username
        }

    }

}