package com.toools.ierp.ui.main

/*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.helpers.ConstantsHelper
import kotlinx.android.synthetic.main.recycler_secciones.view.*

class AdapterSecciones (context: Context, listSecciones: MutableList<ConstantsHelper.Seccion>, isNotificaciones: Boolean, listener: SeccionesListener) :
    RecyclerView.Adapter<AdapterSecciones.SeccionHolder>() {


    private var listSecciones: MutableList<ConstantsHelper.Seccion> = mutableListOf()
    private var context: Context
    private var isNotificaciones: Boolean
    private var listener: SeccionesListener


    init {
        this.listSecciones.addAll(listSecciones)
        this.context = context
        this.isNotificaciones = isNotificaciones
        this.listener = listener
    }

    fun setList(listSecciones: MutableList<ConstantsHelper.Seccion>?) {
        this.listSecciones.clear()
        this.listSecciones.addAll(listSecciones!!)

        notifyDataSetChanged()
    }

    fun setNotificaciones(isNotificaciones: Boolean) {
        this.isNotificaciones = isNotificaciones

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeccionHolder {
        val inflater = LayoutInflater.from(this.context)
        val view = inflater.inflate(R.layout.recycler_secciones, parent, false)
        return SeccionHolder(view)
    }

    override fun getItemCount(): Int {
        return listSecciones.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: SeccionHolder, position: Int) {
        holder.bind(listSecciones[position])

        val seccion = listSecciones[position]

        if (seccion == ConstantsHelper.Seccion.notificaciones) {

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

    class SeccionHolder(view: View) : RecyclerView.ViewHolder(view) {

        var switchNotificaciones = view.notificacionSwitch
        var seccionConstrainLayout = view.seccionConstrainLayout

        fun bind(item: ConstantsHelper.Seccion) = with(itemView) {
            Glide.with(context).load(context.getDrawable(item.icono)).into(iconoImageView)
            iconoImageView.setColorFilter(context.getColor(R.color.white))
            seccionTextView.text = context.getString(item.nombre)

            if (item == ConstantsHelper.Seccion.notificaciones) {
                notificacionSwitch.visibility = View.VISIBLE
            } else {
                notificacionSwitch.visibility = View.GONE
            }
        }

    }

}*/