package com.toools.ierp.ui.tareasFragment

import androidx.fragment.app.Fragment

class TareasFragment : Fragment() {
}

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.*
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.toools.ierp.R
import es.dmoral.toasty.Toasty


const val TAG = "TareasFragment"

class TareasFragment : Fragment(), TareaListener, AddTareaDialogListener {

    private val args: TareasFragmentArgs by navArgs()

    private var act: Activity? = null
    private var adapterProyectos: AdapterProyectoGeneral? = null
    private var adapterTareas: AdapterTareas? = null
    private var onScrollListener: RecyclerView.OnScrollListener? = null
    private var listTareas: MutableList<Tarea> = mutableListOf()
    private var listProyectos: MutableList<Proyecto> = mutableListOf()
    private var idProyectoSelected: String? = null
    private var dialogAddTarea: AddTareaDialog? = null

    private val viewModel: TareasViewModel by lazy {
        ViewModelProvider(this, this.defaultViewModelProviderFactory)[TareasViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.proyectosRecived.observe(this, proyectosObserver)
        viewModel.tareasRecived.observe(this, tareasObserver)
        viewModel.cambioTareaRecived.observe(this, cambioEstadoObserver)
        viewModel.addTareaRecived.observe(this, insertarTareaObserver)

        sharedElementEnterTransition = context?.let { TransitionInflater.from(it).inflateTransition(android.R.transition.move) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            act = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tareas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        act?.let {
            var layoutManager = LinearLayoutManager(it)
            layoutManager.orientation = RecyclerView.HORIZONTAL
            proyectosRecyclerView.layoutManager = layoutManager
            proyectosRecyclerView.setHasFixedSize(true)
            (proyectosRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false

            //calcular el padding para centrar los items
            val padding = ConstantsHelper.getWidhtScreen(it) / 2 - ConstantsHelper.dpToPx(it, 41)
            proyectosRecyclerView.setPadding(padding, 0, padding, 0)
            proyectosRecyclerView.clipToPadding = false

            LinearSnapHelper().attachToRecyclerView(proyectosRecyclerView)

            onScrollListener = object : RecyclerView.OnScrollListener() {

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
            tareasRecyclerView.layoutManager = layoutManager
            tareasRecyclerView.setHasFixedSize(true)
            (tareasRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            args.idProyecto?.let { idProyecto ->
                idProyectoSelected = idProyecto
            } ?: run {
                idProyectoSelected = "-1"
            }

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

                            tareasConstraintLayout.addView(dialog)

                            val constraint = dialog.getViewById(R.id.addTareaConstraintLayout)
                            val params = constraint.layoutParams
                            params.height = tareasConstraintLayout.height
                            params.width = tareasConstraintLayout.width
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
                    tareasRecyclerView.visibility = View.GONE

                    if (emptryView == null) {
                        val inflater = LayoutInflater.from(act)
                        emptryView =
                            inflater.inflate(R.layout.empty_view, tareasConstraintLayout, false)
                    }

                    emptryView?.let { emptryView ->
                        emptyViewConstraintLayout.addView(emptryView)

                        emptryView.tituloEmptyTextView.text = getString(R.string.sin_tareas)
                        emptryView.descripcionEmptyTextView.text = getString(R.string.sin_tareas_desc)

                        Glide.with(context).load(R.drawable.not_tareas).into(emptryView.emptyImageView)
                    }

                } else {

                    emptyViewConstraintLayout.visibility = View.GONE
                    tareasRecyclerView.visibility = View.VISIBLE

                    adapterTareas?.let {
                        it.setList(lista)
                    } ?: run {
                        adapterTareas = AdapterTareas(context, lista, this)
                    }

                    tareasRecyclerView.adapter = adapterTareas
                }
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
        tareasConstraintLayout.removeView(dialogAddTarea)
        act?.let {
            DialogHelper.getInstance().showLoadingAlert(it, null, true)
            viewModel.addTarea(idProyecto = idProyecto, idEmpleado = idEmpleado, titulo = titulo, descripcion = descripcion, plazo = plazo)
        }
    }

    override fun clickCancelTarea() {
        addTareaFloatingActionButton.visibility = View.VISIBLE
        tareasConstraintLayout.removeView(dialogAddTarea)
    }

    //*************************
    //Tareas listener
    //*************************
    override fun aceptarTarea(tarea: Tarea) {
        //mostrar la modal de observaciones
        val inflater = LayoutInflater.from(act)
        val modalObservaciones = inflater.inflate(R.layout.dialog_observaciones, tareasConstraintLayout, false)

        tareasConstraintLayout.addView(modalObservaciones)

        modalObservaciones.emailTextView.text = getString(R.string.title_observacion)
        modalObservaciones.descripcionObsercacionTextView.text = getString(R.string.desc_observacion)

        modalObservaciones.cancelarContraintLayout.setOnClickListener {
            tareasConstraintLayout.removeView(modalObservaciones)
        }

        modalObservaciones.aceptarContraintLayout.setOnClickListener {
            tareasConstraintLayout.removeView(modalObservaciones)
            //llamar a aceptar tarea

            tarea.idTarea?.let {
                viewModel.cambiarEstadoTarea(
                    ConstantsHelper.Estados.abierta.idEstado,
                    tarea.idTarea,
                    modalObservaciones.observacionesEditText.text.toString()
                )
            }
        }
    }

    override fun cambiarEstadoTarea(tarea: Tarea, estado: ConstantsHelper.Estados) {
        //mostrar la modal de observaciones
        val inflater = LayoutInflater.from(act)
        val modalObservaciones = inflater.inflate(R.layout.dialog_observaciones, tareasConstraintLayout, false)

        tareasConstraintLayout.addView(modalObservaciones)

        modalObservaciones.emailTextView.text = getString(R.string.title_observacion)
        modalObservaciones.descripcionObsercacionTextView.text = getString(R.string.desc_observacion)

        modalObservaciones.cancelarContraintLayout.setOnClickListener {
            tareasConstraintLayout.removeView(modalObservaciones)
        }

        modalObservaciones.aceptarContraintLayout.setOnClickListener {
            tareasConstraintLayout.removeView(modalObservaciones)
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
    //Observers
    //*************************
    private val proyectosObserver = Observer<Resource<ProyectosResponse>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(TAG, "proyectos: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, true)
                }
            }
            Resource.Status.SUCCESS -> {
                act?.let {

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

    private val tareasObserver = Observer<Resource<TareasResponse>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(TAG, "proyectos: {${resource.status}}")
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

                        val index = listProyectos.indexOfFirst { proyecto -> proyecto.id == idProyecto }
                        proyectosRecyclerView.scrollToPosition(index)
                        adapterProyectos?.let { adapterProyectos ->
                            adapterProyectos.setPositionSelected(index)
                        }
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
            Log.e(TAG, "proyectos: {${resource.status}}")
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
            Log.e(TAG, "addTarea: {${resource.status}}")
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
                            findNavController().navigate(R.id.tareasAsignadasFragment)
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
} */