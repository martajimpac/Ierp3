package com.toools.ierp.ui.fichajesFragment

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toools.ierp.R
import com.toools.ierp.data.model.LoginResponse
import com.toools.ierp.databinding.RecyclerAccionesBinding

class AdapterAcciones(context: Context, listMomentos: MutableList<LoginResponse.Momentos>) :
    RecyclerView.Adapter<AdapterAcciones.AccionesHolder>() {

    private var listMon: MutableList<LoginResponse.Momentos> = mutableListOf()
    private var context: Context
    private lateinit var binding: RecyclerAccionesBinding


    init {
        this.listMon.addAll(listMomentos)
        this.context = context
    }

    fun setList(listMomentos: MutableList<LoginResponse.Momentos>?) {
        this.listMon.clear()
        this.listMon.addAll(listMomentos!!)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccionesHolder {
        binding = RecyclerAccionesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AccionesHolder(binding)
    }

    override fun getItemCount(): Int {
        return listMon.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: AccionesHolder, position: Int) {
        holder.bind(listMon[position])
    }

    class AccionesHolder(val binding:RecyclerAccionesBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("RtlHardcoded")
        fun bind(item: LoginResponse.Momentos) = with(itemView) {
            binding.apply{
                if(item.tipo == LoginResponse.Momentos.entrada){
                    txtAccion.text = resources.getString(R.string.entrada)
                    txtAccion.setTextColor(resources.getColor(R.color.colorPrimary,null))
                }else{
                    txtAccion.text = resources.getString(R.string.salida)
                    txtAccion.setTextColor(resources.getColor(R.color.red_app,null))
                }
                txtHora.text = item.getHoraToString()
            }
        }
    }
}