package com.toools.ierp.ui.tareasFragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.*
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.core.*
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.*
import com.toools.ierp.databinding.FragmentTareasBinding
import com.toools.ierp.ui.adapter.AdapterProyectoGeneral
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

const val TAG = "TareasFragment"
@AndroidEntryPoint
class TareasFragment : Fragment(), TareaListener, AddTareaDialogListener {

    private val args: TareasFragmentArgs by navArgs()

    private lateinit var binding: FragmentTareasBinding

    private var act: Activity? = null
    private var adapterProyectos: AdapterProyectoGeneral? = null
    private var adapterTareas: AdapterTareas? = null
    private var onScrollListener: RecyclerView.OnScrollListener? = null
    private var listTareas: MutableList<Tarea> = mutableListOf()
    private var listProyectos: MutableList<Proyecto> = mutableListOf()
    private var idProyectoSelected: String? = null
    private var dialogAddTarea: AddTareaDialog? = null

    private val viewModel: TareasViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = context?.let { TransitionInflater.from(it).inflateTransition(android.R.transition.move) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            act = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = FragmentTareasBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.apply {
            var layoutManager = LinearLayoutManager(requireContext())
            layoutManager.orientation = RecyclerView.HORIZONTAL
            proyectosRecyclerView.layoutManager = layoutManager
            proyectosRecyclerView.setHasFixedSize(true)
            (proyectosRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            //calcular el padding para centrar los items
            val padding = ConstantHelper.getWidhtScreen(requireActivity()) / 2 - ConstantHelper.dpToPx(requireContext(), 41)

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

            layoutManager = LinearLayoutManager(requireContext())
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

                            dialog.setAddClickListener(this@TareasFragment)
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
            setUpObservers()
        }
    }

    private fun onLoadView(){
        DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
        viewModel.proyectos()
    }

    //*************************
    //Class Metodos
    //*************************

    private fun filterTareas(idProyecto: String?){
        binding.apply {

            listTareas.filter { tarea -> tarea.idProyecto == idProyecto || idProyecto == "-1" }.toMutableList().let { lista ->
                if (lista.isEmpty()) {

                    emptyViewConstraintLayout.visibility = View.VISIBLE
                    tareasRecyclerView.visibility = View.GONE

                    emptyView.tituloEmptyTextView.text = getString(R.string.sin_tareas)
                    emptyView.descripcionEmptyTextView.text = getString(R.string.sin_tareas_desc)

                    Glide.with(requireContext()).load(R.drawable.not_tareas).into(emptyView.emptyImageView)

                } else {

                    emptyViewConstraintLayout.visibility = View.GONE
                    tareasRecyclerView.visibility = View.VISIBLE

                    adapterTareas?.let {
                        it.setList(lista)
                    } ?: run {
                        adapterTareas = AdapterTareas(requireContext(), lista, this@TareasFragment)
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
        binding.addTareaFloatingActionButton.visibility = View.VISIBLE
        binding.tareasConstraintLayout.removeView(dialogAddTarea)

        DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
        viewModel.insertarTarea(idProyecto = idProyecto, idEmpleado = idEmpleado, titulo = titulo, descripcion = descripcion, plazo = plazo)
    }

    override fun clickCancelTarea() {
        binding.addTareaFloatingActionButton.visibility = View.VISIBLE
        binding.tareasConstraintLayout.removeView(dialogAddTarea)
    }

    //*************************
    //Tareas listener
    //*************************
    //llamamos a estas funciones desde el adapter
    override fun aceptarTarea(tarea: Tarea) {
        //mostrar la modal de observaciones
        binding.dialogObservaciones.apply{

            binding.modalObservacionesTareas.visibility = View.VISIBLE

            emailTextView.text = getString(R.string.title_observacion)
            descripcionObsercacionTextView.text = getString(R.string.desc_observacion)
            cancelarContraintLayout.setOnClickListener {
                binding.modalObservacionesTareas.visibility = View.GONE
            }

            aceptarContraintLayout.setOnClickListener {
                binding.modalObservacionesTareas.visibility = View.GONE
                //llamar a aceptar tarea

                tarea.idTarea?.let {
                    viewModel.cambioEstadoTarea(
                        tarea.idTarea,
                        ConstantHelper.Estados.abierta.idEstado,
                        observacionesEditText.text.toString()
                    )
                }
            }
        }
    }

    override fun cambiarEstadoTarea(tarea: Tarea, estado: ConstantHelper.Estados) {
        //mostrar la modal de observaciones
        binding.dialogObservaciones.apply{

            binding.modalObservacionesTareas.visibility = View.VISIBLE

            emailTextView.text = getString(R.string.title_observacion)
            descripcionObsercacionTextView.text = getString(R.string.desc_observacion)

            cancelarContraintLayout.setOnClickListener {
                binding.modalObservacionesTareas.visibility = View.GONE
            }

            aceptarContraintLayout.setOnClickListener {
                binding.modalObservacionesTareas.visibility = View.GONE
                tarea.idTarea?.let {
                    viewModel.cambioEstadoTarea(
                        tarea.idTarea,
                        estado.idEstado,
                        observacionesEditText.text.toString()
                    )
                }
            }
        }
    }

    //*************************
    //Observers
    //*************************

    fun setUpObservers() {
        viewModel.proyectosLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "proyectos: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    (response.data?.proyectos?.sortedBy{ proyecto -> proyecto.nombre })?.toMutableList()?.let { listProyectos ->
                        val todos = Proyecto("-1", "TODOS", null, null, null, "TODOS", mutableListOf())
                        listProyectos.add(0, todos)

                        this.listProyectos = listProyectos

                        adapterProyectos?.setList(listProyectos) ?: run {
                            adapterProyectos = AdapterProyectoGeneral(requireContext(), listProyectos) { position ->
                                binding.proyectosRecyclerView.smoothScrollToPosition(position)
                            }
                        }

                        binding.proyectosRecyclerView.adapter = adapterProyectos
                        viewModel.tareas()

                    }
                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.proyectosError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            viewModel.proyectos()
                        })
                }
            }
        }
        viewModel.tareasLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "tareas: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)

                    response.data?.tareas?.let{ listTareas ->
                        this.listTareas = listTareas.toMutableList()
                    }

                    idProyectoSelected?.let{ idProyecto ->

                        val index = listProyectos.indexOfFirst { proyecto -> proyecto.id == idProyecto }
                        binding.proyectosRecyclerView.scrollToPosition(index)
                        adapterProyectos?.setPositionSelected(index)
                        filterTareas(idProyecto = idProyecto)

                    } ?: run {
                        filterTareas(idProyecto = listProyectos[0].id)
                    }
                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                        title = R.string.ups,
                        text = response.exception?: ErrorHelper.tareasError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            viewModel.tareas()
                        }
                    )
                }
            }
        }
        viewModel.cambioEstadoTareaLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "cambiar_estado_tarea: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    Toasty.success(requireContext(), resources.getString(R.string.cambio_tarea_ok)).show()
                    viewModel.tareas()
                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.cambioEstadoTareaError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {}
                    )
                }
            }
        }
        viewModel.insertarTareaLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "insertarTarea: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                        title = R.string.tarea_insertada,
                        text = R.string.desc_tarea_insertada,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            findNavController().navigate(R.id.tareasAsignadasFragment)
                        })
                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.insertarTareaError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {}
                    )
                }
            }
        }
    }
} 