package com.toools.ierp.ui.respuestasFragment

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.toools.ierp.R
import com.toools.ierp.data.model.Respuesta
import com.toools.ierp.databinding.RecyclerRespuestasBinding

class AdapterRespuestas(context: Context, listRespuestas: MutableList<Respuesta>) :
    RecyclerView.Adapter<AdapterRespuestas.RespuestaHolder>() {

    private lateinit var binding: RecyclerRespuestasBinding
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
        binding = RecyclerRespuestasBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RespuestaHolder(binding)
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

    class RespuestaHolder(val binding: RecyclerRespuestasBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Respuesta) = with(itemView) {
            binding.apply {
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
    }
}