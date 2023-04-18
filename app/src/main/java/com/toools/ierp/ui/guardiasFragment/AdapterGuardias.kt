package com.toools.ierp.ui.guardiasFragment

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toools.ierp.R
import com.toools.ierp.data.model.GuardiasResponse.Guardias
import com.toools.ierp.databinding.RecyclerGuardiasBinding


class AdapterGuardias : RecyclerView.Adapter<AdapterGuardias.GuardiasHolder>{

    private val ctx: Context
    private var listGuardias: MutableList<Guardias> = mutableListOf()
    private lateinit var binding: RecyclerGuardiasBinding


    constructor(context: Context, listGuardias: MutableList<Guardias>) {
        ctx = context
        this.listGuardias.addAll(listGuardias)
    }

    fun setList(listGuardias: MutableList<Guardias>?) {

        if (listGuardias != null)
            this.listGuardias.clear()
        else
            this.listGuardias = mutableListOf()

        this.listGuardias.addAll(listGuardias!!)

        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuardiasHolder {
        binding = RecyclerGuardiasBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GuardiasHolder(binding)
    }

    override fun getItemCount(): Int {
        return listGuardias.size
    }

    override fun onBindViewHolder(holder: GuardiasHolder, position: Int) {
        holder.bind(listGuardias[position])
    }

    class GuardiasHolder(val binding: RecyclerGuardiasBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("RtlHardcoded")
        fun bind(item: Guardias) = with(itemView) {
            binding.apply{
                if(item.tipo == Guardias.entrada){
                    txtAccion.text = resources.getString(R.string.entrada)
                    txtAccion.setTextColor(resources.getColor(R.color.colorPrimary,null))
                }else{
                    txtAccion.text = resources.getString(R.string.salida)
                    txtAccion.setTextColor(resources.getColor(R.color.red_app,null))
                }

                txtHora.text = item.getHoraToString()

                txtDescripcion.text = item.descripcion

            }
        }

    }

}