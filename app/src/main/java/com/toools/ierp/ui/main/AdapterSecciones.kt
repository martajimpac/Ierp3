package com.toools.ierp.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.databinding.RecyclerSeccionesBinding

class AdapterSecciones (context: Context, listSecciones: MutableList<ConstantHelper.Seccion>, isNotificaciones: Boolean, listener: SeccionesListener) :
    RecyclerView.Adapter<AdapterSecciones.SeccionHolder>() {

    private lateinit var binding: RecyclerSeccionesBinding

    private var listSecciones: MutableList<ConstantHelper.Seccion> = mutableListOf()
    private var context: Context
    private var isNotificaciones: Boolean
    private var listener: SeccionesListener


    init {
        this.listSecciones.addAll(listSecciones)
        this.context = context
        this.isNotificaciones = isNotificaciones
        this.listener = listener
    }

    fun setList(listSecciones: MutableList<ConstantHelper.Seccion>?) {
        this.listSecciones.clear()
        this.listSecciones.addAll(listSecciones!!)

        notifyDataSetChanged()
    }

    fun setNotificaciones(isNotificaciones: Boolean) {
        this.isNotificaciones = isNotificaciones

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeccionHolder {
        binding = RecyclerSeccionesBinding.inflate(LayoutInflater.from(parent.context),parent,false) //esto creo que es asi pero no se porque
        return SeccionHolder(binding)
    }

    override fun getItemCount() = listSecciones.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: SeccionHolder, position: Int) {
        holder.bind(listSecciones[position],context)

        val seccion = listSecciones[position]

        if (seccion == ConstantHelper.Seccion.notificaciones) {

            holder.switchNotificaciones.isChecked = isNotificaciones

            holder.switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != isNotificaciones) {
                    listener.changeNotificaciones(isChecked)
                }
            }

        } else {

            holder.seccionConstrainLayout.setOnClickListener {
                listener.selectedSeccion(seccion)
            }
        }
    }

    class SeccionHolder(val binding: RecyclerSeccionesBinding) : RecyclerView.ViewHolder(binding.root) {

        var switchNotificaciones = binding.notificacionSwitch
        var seccionConstrainLayout = binding.seccionConstrainLayout

        fun bind(item: ConstantHelper.Seccion, context: Context) {
            binding.apply{
                Glide.with(context).load(context.getDrawable(item.icono)).into(iconoImageView)
                iconoImageView.setColorFilter(context.getColor(R.color.white))
                seccionTextView.text = context.getString(item.nombre)

                if (item == ConstantHelper.Seccion.notificaciones) {
                    notificacionSwitch.visibility = View.VISIBLE
                } else {
                    notificacionSwitch.visibility = View.GONE
                }
            }
        }

    }

}