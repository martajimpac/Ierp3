package com.toools.ierp.ui.soportesFragment

/*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.entities.ierp.Soporte
import kotlinx.android.synthetic.main.recycler_soportes.view.*

class AdapterSoportes (context: Context, listSoportes: MutableList<Soporte>, var listener: SoportesListener) :
    RecyclerView.Adapter<AdapterSoportes.SoporteHolder>() {

    private var listSoportes: MutableList<Soporte> = mutableListOf()
    private var context: Context

    init {
        this.listSoportes.addAll(listSoportes)
        this.context = context
    }

    fun setList(listSoportes: MutableList<Soporte>?) {
        this.listSoportes.clear()
        this.listSoportes.addAll(listSoportes!!)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoporteHolder {
        val inflater = LayoutInflater.from(this.context)
        val view = inflater.inflate(R.layout.recycler_soportes, parent, false)
        return SoporteHolder(view)
    }

    override fun getItemCount(): Int {
        return listSoportes.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: SoporteHolder, position: Int) {
        holder.bind(listSoportes[position])

        val soporte = listSoportes[position]

        holder.asignarCardView.setOnClickListener {
            listener.clickAsignarSoporte(soporte)
        }

        holder.soporteCardView.setOnClickListener {
            listener.clickSoporte(soporte)
        }
    }

    class SoporteHolder(view: View) : RecyclerView.ViewHolder(view) {

        var asignarCardView = view.asignarCardView
        var soporteCardView = view.soporteCardView

        fun bind(item: Soporte) = with(itemView) {
            Glide.with(context).load(item.imagenAsignado).error(Glide.with(context).load(R.drawable.luciano).circleCrop()).circleCrop().into(asignadoImageView)
            Glide.with(context).load(item.imgMiniProyecto).error(Glide.with(context).load(R.drawable.ic_proyectos).circleCrop()).circleCrop().into(proyectoImageView)

            emailTextView.text = item.emailUserIncidencia
            respuestaTextView.text = item.getFechaFormateada()
            tituloTextView.text = item.titulo

            if (item.estado == Soporte.sinAsignar) {
                asignarCardView.visibility = View.VISIBLE
                asignadoImageView.visibility = View.GONE

                estadoTextView.text = context.getString(R.string.sin_abrir)
                estadoCardView.setCardBackgroundColor(context.getColor(R.color.soporte_sin_abrir))

            } else {
                asignarCardView.visibility = View.GONE
                asignadoImageView.visibility = View.VISIBLE

                estadoTextView.text = context.getString(R.string.abierta)
                estadoCardView.setCardBackgroundColor(context.getColor(R.color.soporte_abierto))
            }

        }

    }

}*/