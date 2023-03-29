package com.toools.ierp.ui.tareasAsignadasFragment

import androidx.fragment.app.Fragment
class TareasAsignadasFragment : Fragment(){ }
/*
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.custom.AddTareaDialog
import com.toools.ierp.custom.AddTareaDialogListener
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.RestBaseObject
import com.toools.ierp.entities.ierp.*
import com.toools.ierp.helpers.ConstantsHelper
import com.toools.ierp.helpers.DialogHelper
import com.toools.ierp.helpers.rest.ErrorHelper
import com.toools.ierp.ui.adapter.AdapterProyectoGeneral
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialog_observaciones.view.*
import kotlinx.android.synthetic.main.empty_view.view.*
import kotlinx.android.synthetic.main.fragment_tareas_asignadas.*
import kotlinx.android.synthetic.main.fragment_tareas_asignadas.addTareaFloatingActionButton
import kotlinx.android.synthetic.main.fragment_tareas_asignadas.emptyViewConstraintLayout
import kotlinx.android.synthetic.main.fragment_tareas_asignadas.proyectosRecyclerView

const val TAG = "TareasAsignadasFragment"

class TareasAsignadasFragment : Fragment(), AddTareaDialogListener {

    private var act: Activity? = null
    private var adapterProyectos: AdapterProyectoGeneral? = null
    private var adapterTareas: AdapterTareasAsignadas? = null
    private var onScrollListener: RecyclerView.OnScrollListener? = null
    private var listTareas: MutableList<TareaAsignada> = mutableListOf()
    private var listProyectos: MutableList<Proyecto> = mutableListOf()
    private var idProyectoSelected: String? = null
    private var dialogAddTarea: AddTareaDialog? = null

    private val viewModel: TareasAsignadasViewModel by lazy {
        ViewModelProvider(this, this.defaultViewModelProviderFactory)[TareasAsignadasViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
        viewModel.proyectosRecived.observe(this, proyectosObserver)
        viewModel.tareasRecived.observe(this, tareasObserver)
        viewModel.cambioTareaRecived.observe(this, cambioEstadoObserver)
        viewModel.addTareaRecived.observe(this, insertarTareaObserver)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            act = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tareas_asignadas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        act?.let {
            var layoutManager = LinearLayoutManager(it)
            layoutManager.orientation = RecyclerView.HORIZONTAL
            proyectosRecyclerView.layoutManager = layoutManager
            proyectosRecyclerView.setHasFixedSize(true)
            (proyectosRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            //calcular el padding para centrar los items
            val padding = ConstantsHelper.getWidhtScreen(it) / 2 - ConstantsHelper.dpToPx(it, 41)
            proyectosRecyclerView.setPadding(padding,0,padding,0)
            proyectosRecyclerView.clipToPadding = false

            LinearSnapHelper().attachToRecyclerView(proyectosRecyclerView)

            onScrollListener = object: RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                    adapterProyectos?.let { adapterProyectos ->
                        val position = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        adapterProyectos.setPositionSelected(position)
                        idProyectoSelected = listProyectos[position].id
                        filterTareas(idProyectoSelected)
                    }
                }
            }

            onScrollListener?.let { listener ->
                proyectosRecyclerView.addOnScrollListener(listener)
            }

            layoutManager = LinearLayoutManager(it)
            layoutManager.orientation = RecyclerView.VERTICAL
            tareasAsignadasRecyclerView.layoutManager = layoutManager
            tareasAsignadasRecyclerView.setHasFixedSize(true)
            (tareasAsignadasRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            idProyectoSelected = "-1"

            onLoadView()
            addTareaFloatingActionButton.setOnClickListener {

                act?.let { act ->
                    if (idProyectoSelected == "-1") {

                        DialogHelper.getInstance().showOKAlert(activity = act,
                            title = R.string.add_tarea_sin_proyecto,
                            text = R.string.add_tarea_sin_proyecto_desc,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {

                            })
                    } else {

                        if (dialogAddTarea == null) {
                            dialogAddTarea = AddTareaDialog(act as Context)
                        }

                        dialogAddTarea?.let { dialog ->

                            addTareaFloatingActionButton.visibility = View.GONE

                            val proyecto = listProyectos.first { proyecto -> proyecto.id == idProyectoSelected }

                            dialog.setAddClickListener(this)
                            dialog.setProyecto(proyecto)
                            dialog.setTitulo(
                                getString(
                                    R.string.titulo_add_proyecto,
                                    proyecto.nombre
                                )
                            )

                            tareasAsignadasConstraintLayout.addView(dialog)

                            val constraint = dialog.getViewById(R.id.addTareaConstraintLayout)
                            val params = constraint.layoutParams
                            params.height = tareasAsignadasConstraintLayout.height
                            params.width = tareasAsignadasConstraintLayout.width
                            constraint.layoutParams = params

                        }
                    }
                }
            }
        }
    }

    private fun onLoadView(){
        act?.let {
            DialogHelper.getInstance().showLoadingAlert(it, null, true)
            viewModel.callProyectos()
        }
    }

    //*************************
    //Class Metodos
    //*************************
    var emptryView: View? = null
    private fun filterTareas(idProyecto: String?){
        act?.let { context ->

            emptryView?.let {
                emptyViewConstraintLayout.removeView(it)
            }

            listTareas.filter { tarea -> tarea.idProyecto == idProyecto || idProyecto == "-1" }.toMutableList().let { lista ->
                if (lista.isEmpty()) {

                    emptyViewConstraintLayout.visibility = View.VISIBLE
                    tareasAsignadasRecyclerView.visibility = View.GONE

                    if (emptryView == null) {
                        val inflater = LayoutInflater.from(act)
                        emptryView =
                            inflater.inflate(R.layout.empty_view, tareasAsignadasConstraintLayout, false)
                    }

                    emptryView?.let { emptryView ->
                        emptyViewConstraintLayout.addView(emptryView)

                        emptryView.tituloEmptyTextView.text = getString(R.string.sin_tareas)
                        emptryView.descripcionEmptyTextView.text = getString(R.string.sin_tareas_desc)

                        Glide.with(context).load(R.drawable.not_tareas).into(emptryView.emptyImageView)
                    }

                } else {

                    emptyViewConstraintLayout.visibility = View.GONE
                    tareasAsignadasRecyclerView.visibility = View.VISIBLE

                    adapterTareas?.let {
                        it.setList(lista)
                    } ?: run {
                        adapterTareas = AdapterTareasAsignadas(context, lista) { tarea ->
                            cambiarEstadoTarea(tarea, ConstantsHelper.Estados.completada)
                        }
                    }

                    tareasAsignadasRecyclerView.adapter = adapterTareas
                }
            }
        }
    }

    fun cambiarEstadoTarea(tarea: TareaAsignada, estado: ConstantsHelper.Estados) {

        //mostrar la modal de observaciones
        val inflater = LayoutInflater.from(act)
        val modalObservaciones = inflater.inflate(R.layout.dialog_observaciones, tareasAsignadasConstraintLayout, false)

        tareasAsignadasConstraintLayout.addView(modalObservaciones)

        modalObservaciones.emailTextView.text = getString(R.string.title_observacion)
        modalObservaciones.descripcionObsercacionTextView.text = getString(R.string.desc_observacion)

        modalObservaciones.cancelarContraintLayout.setOnClickListener {
            tareasAsignadasConstraintLayout.removeView(modalObservaciones)
        }

        modalObservaciones.aceptarContraintLayout.setOnClickListener {
            tareasAsignadasConstraintLayout.removeView(modalObservaciones)
            tarea.idTarea?.let {
                viewModel.cambiarEstadoTarea(
                    estado.idEstado,
                    tarea.idTarea,
                    modalObservaciones.observacionesEditText.text.toString()
                )
            }
        }
    }

    //*************************
    //AddTareas listener
    //*************************
    override fun clickAddTarea(
        idProyecto: String,
        idEmpleado: String,
        titulo: String,
        descripcion: String,
        plazo: String
    ) {
        addTareaFloatingActionButton.visibility = View.VISIBLE
        tareasAsignadasConstraintLayout.removeView(dialogAddTarea)
        act?.let {
            DialogHelper.getInstance().showLoadingAlert(it, null, true)
            viewModel.addTarea(idProyecto = idProyecto, idEmpleado = idEmpleado, titulo = titulo, descripcion = descripcion, plazo = plazo)
        }
    }

    override fun clickCancelTarea() {
        addTareaFloatingActionButton.visibility = View.VISIBLE
        tareasAsignadasConstraintLayout.removeView(dialogAddTarea)
    }

    //*************************
    //Observers
    //*************************
    private val proyectosObserver = Observer<Resource<ProyectosResponse>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(com.toools.ierp.ui.tareasFragment.TAG, "proyectos: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, true)
                }

            }
            Resource.Status.SUCCESS -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)

                    (resource.data?.proyectos?.sortedBy{ proyecto -> proyecto.nombre })?.toMutableList()?.let { listProyectos ->

                        val todos = Proyecto("-1", "TODOS", null, null, null, "TODOS", mutableListOf())
                        listProyectos.add(0, todos)

                        this.listProyectos = listProyectos

                        adapterProyectos?.setList(listProyectos) ?: run {

                            adapterProyectos = AdapterProyectoGeneral(it, listProyectos) { position ->
                                proyectosRecyclerView.smoothScrollToPosition(position)
                            }
                        }

                        proyectosRecyclerView.adapter = adapterProyectos

                        viewModel.callTareas()

                    }
                }
            }
            Resource.Status.ERROR -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = it,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.proyectosError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            viewModel.callProyectos()
                        })
                }
            }
        }
    }

    private val tareasObserver = Observer<Resource<TareasAsignadasResponse>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(com.toools.ierp.ui.tareasFragment.TAG, "proyectos: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, true)
                }
            }
            Resource.Status.SUCCESS -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)

                    resource.data?.tareas?.let{ listTareas ->
                        this.listTareas = listTareas.toMutableList()
                    }

                    idProyectoSelected?.let{ idProyecto ->
                        filterTareas(idProyecto = idProyecto)
                    } ?: run {
                        filterTareas(idProyecto = listProyectos[0].id)
                    }
                }
            }
            Resource.Status.ERROR -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = it,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.tareasError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            viewModel.callTareas()
                        })
                }
            }
        }
    }

    private val cambioEstadoObserver = Observer<Resource<RestBaseObject>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(com.toools.ierp.ui.tareasFragment.TAG, "proyectos: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, true)
                }

            }
            Resource.Status.SUCCESS -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)

                    Toasty.success(it, resources.getString(R.string.cambio_tarea_ok)).show()

                    viewModel.callTareas()
                }
            }
            Resource.Status.ERROR -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = it,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.cambioEstadoTareaError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {

                        })
                }
            }
        }
    }

    private val insertarTareaObserver = Observer<Resource<RestBaseObject>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(com.toools.ierp.ui.tareasFragment.TAG, "addTarea: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, true)
                }

            }
            Resource.Status.SUCCESS -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)

                    DialogHelper.getInstance().showOKAlert(activity = it,
                        title = R.string.tarea_insertada,
                        text = R.string.desc_tarea_insertada,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            onLoadView()
                        })
                }
            }
            Resource.Status.ERROR -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = it,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.insertarTareaError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {

                        })
                }
            }
        }
    }
}*/