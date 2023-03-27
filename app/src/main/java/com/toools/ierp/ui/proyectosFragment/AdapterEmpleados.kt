package com.toools.ierp.ui.proyectosFragment

/*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.entities.ierp.Usuario
import kotlinx.android.synthetic.main.recycler_empleado.view.*

class AdapterEmpleados (context: Context, listEmpleados: MutableList<Usuario>) :
    RecyclerView.Adapter<AdapterEmpleados.UsuarioHolder>() {


    private var listEmpleados: MutableList<Usuario> = mutableListOf()
    private var context: Context

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
        val inflater = LayoutInflater.from(this.context)
        val view = inflater.inflate(R.layout.recycler_empleado, parent, false)
        return UsuarioHolder(view)
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

    class UsuarioHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: Usuario) = with(itemView) {
            Glide.with(context).load(item.imagen).circleCrop().into(empleadoImageView)
            nombreTextView.text = item.username
        }

    }

}*/