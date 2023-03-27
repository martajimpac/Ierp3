package com.toools.ierp.ui.fichajesFragment

/*
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.toools.ierp.R

class AdapterAcciones(context: Context, listMomentos: MutableList<Momentos>) :
    RecyclerView.Adapter<AdapterAcciones.MomentoHolder>() {


    private var listMon: MutableList<Momentos> = mutableListOf()
    private var context: Context


    init {
        this.listMon.addAll(listMomentos)
        this.context = context
    }

    fun setList(listMomentos: MutableList<Momentos>?) {
        this.listMon.clear()
        this.listMon.addAll(listMomentos!!)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentoHolder {
        val inflater = LayoutInflater.from(this.context)
        val view = inflater.inflate(R.layout.recycler_momentos, parent, false)
        return MomentoHolder(view)
    }

    override fun getItemCount(): Int {
        return listMon.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: MomentoHolder, position: Int) {
        holder.bind(listMon[position])
    }

    class MomentoHolder(view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("RtlHardcoded")
        fun bind(item: Momentos) = with(itemView) {
            if(item.tipo == Momentos.entrada){
                txtAccion.text = resources.getString(R.string.entrada)
                txtAccion.setTextColor(resources.getColor(R.color.colorPrimary,null))
            }else{
                txtAccion.text = resources.getString(R.string.salida)
                txtAccion.setTextColor(resources.getColor(R.color.red_app,null))
            }

            txtHora.text = item.getHoraToString()

        }
    }
}*/