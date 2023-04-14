package com.toools.ierp.core

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.toools.ierp.R
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.Proyecto
import com.toools.ierp.data.model.Usuario
import com.toools.ierp.databinding.DialogAddTareaBinding
import java.util.*

class AddTareaDialog @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, defStyleRes: Int = 0):
    ConstraintLayout(context, attrs, defStyle, defStyleRes) {

    private var binding: DialogAddTareaBinding

    private var listener: AddTareaDialogListener? = null
    private var adapterEmpleados: AdapterEmpleados? = null
    private var listEmpleados: MutableList<Usuario> = mutableListOf()
    private var onScrollListener: RecyclerView.OnScrollListener? = null
    private var proyecto: Proyecto? = null

    private var dia : Int = 1
    private var mes : Int = 7
    private var anyo : Int = 2019

    private var idEmpleado: String? = ""

    init {

        // todo no estoy segura si aqui se pone this
        val inflater = LayoutInflater.from(this.context)
        binding = DialogAddTareaBinding.inflate(inflater, this,false)

        addView(binding.root)

        onViewLoad()
    }

    fun setAddClickListener(listener: AddTareaDialogListener){
        this.listener = listener
    }

    fun setTitulo(titulo: String) {
        binding.tituloTextView.text = titulo
    }

    fun setProyecto(proyecto: Proyecto){

        resetValores()

        this.listEmpleados = proyecto.usuarios.toMutableList()
        this.proyecto = proyecto

        adapterEmpleados?.let {
            it.setList(listEmpleados)
        } ?: run {
            adapterEmpleados = AdapterEmpleados(context, listEmpleados)  { position ->
                binding.empleadosRecyclerView.smoothScrollToPosition(position)
            }
        }

        binding.empleadosRecyclerView.adapter = adapterEmpleados

        binding.empleadosRecyclerView.smoothScrollToPosition(0)
    }

    @SuppressLint("SetTextI18n")
    fun resetValores() {
        binding.apply{
            tituloEditText.setText("")
            descripcionEditText.setText("")

            val cal = Calendar.getInstance()
            anyo = cal.get(Calendar.YEAR)
            mes = cal.get(Calendar.MONTH) + 1
            dia = cal.get(Calendar.DAY_OF_MONTH)

            fechaTextView.text = "$dia/$mes/$anyo"
        }
    }

    @SuppressLint("SetTextI18n")
    fun onViewLoad() {
        binding.apply {
            errorTextView.visibility = View.GONE

            val layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = RecyclerView.HORIZONTAL
            empleadosRecyclerView.layoutManager = layoutManager
            empleadosRecyclerView.setHasFixedSize(true)
            (empleadosRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            //calcular el padding para centrar los items
            val padding = ConstantHelper.getWidhtScreen(context) / 2 - ConstantHelper.dpToPx(context, 57)
            empleadosRecyclerView.setPadding(padding,0,padding,0)
            empleadosRecyclerView.clipToPadding = false

            LinearSnapHelper().attachToRecyclerView(empleadosRecyclerView)

            onScrollListener = object: RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                    adapterEmpleados?.let { adapter ->
                        val position = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        idEmpleado = if (position > 0 && position < listEmpleados.size) {
                            adapter.setPositionSelected(position)
                            listEmpleados[position].id
                        } else {
                            adapter.setPositionSelected(0)
                            listEmpleados[0].id
                        }
                    }
                }
            }

            onScrollListener?.let { listener ->
                empleadosRecyclerView.addOnScrollListener(listener)
            }

            fechaConstraintLayout.setOnClickListener {

                DatePickerDialog(context, R.style.DatePickerDialogTheme,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

                        anyo = year
                        mes = month + 1
                        dia = dayOfMonth

                        fechaTextView.text = "$dia/$mes/$anyo"

                    }, anyo, mes-1, dia
                ).show()
            }

            aceptarContraintLayout.setOnClickListener {
                idEmpleado?.let { idEmpleado ->
                    if (tituloEditText.text.toString().isEmpty()) {
                        errorTextView.visibility = View.VISIBLE
                    } else {
                        listener?.clickAddTarea(
                            proyecto?.id!!,
                            idEmpleado,
                            tituloEditText.text.toString(),
                            descripcionEditText.text.toString(),
                            "$dia/$mes/$anyo"
                        )
                    }
                } ?: run{
                    errorTextView.visibility = View.VISIBLE
                }
            }

            cancelarContraintLayout.setOnClickListener {
                listener?.clickCancelTarea()
            }
        }
    }
}

interface AddTareaDialogListener {
    fun clickAddTarea(idProyecto: String, idEmpleado: String, titulo: String, descripcion: String, plazo: String)
    fun clickCancelTarea()
}