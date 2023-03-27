package com.toools.ierp.ui.respuestasFragment

/*
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.entities.ierp.Respuesta
import kotlinx.android.synthetic.main.recycler_respuestas.view.*

class AdapterRespuestas(context: Context, listRespuestas: MutableList<Respuesta>) :
    RecyclerView.Adapter<AdapterRespuestas.RespuestaHolder>() {

    private var listRespuestas: MutableList<Respuesta> = mutableListOf()
    private var context: Context

    init {
        this.listRespuestas.addAll(listRespuestas)
        this.context = context
    }

    fun setList(listRespuestas: MutableList<Respuesta>?) {
        this.listRespuestas.clear()
        this.listRespuestas.addAll(listRespuestas!!)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RespuestaHolder {
        val inflater = LayoutInflater.from(this.context)
        val view = inflater.inflate(R.layout.recycler_respuestas, parent, false)
        return RespuestaHolder(view)
    }

    override fun getItemCount(): Int {
        return listRespuestas.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RespuestaHolder, position: Int) {
        holder.bind(listRespuestas[position])

    }

    class RespuestaHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: Respuesta) = with(itemView) {
            item.imagenUsuario?.let {
                Glide.with(context).load(item.imagenUsuario).circleCrop().into(autorImageView)
            } ?: run {
                Glide.with(context).load(R.drawable.luciano).circleCrop().into(autorImageView)
            }

            emailTextView.text = item.emailAutor
            fechaTextView.text = item.getFechaFormateada()
            respuestaTextView.text = Html.fromHtml(item.respuesta, Html.FROM_HTML_MODE_LEGACY)
        }

    }

}*/