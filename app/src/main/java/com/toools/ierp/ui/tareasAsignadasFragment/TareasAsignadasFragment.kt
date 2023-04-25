package com.toools.ierp.ui.tareasAsignadasFragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.databinding.FragmentTareasAsignadasBinding
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.core.*
import com.toools.ierp.data.model.*
import com.toools.ierp.ui.adapter.AdapterProyectoGeneral
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

const val TAG = "TareasAsignadasFragment"

@AndroidEntryPoint
class TareasAsignadasFragment : Fragment(), AddTareaDialogListener {

    private var act: Activity? = null
    private var adapterProyectos: AdapterProyectoGeneral? = null
    private var adapterTareas: AdapterTareasAsignadas? = null
    private var onScrollListener: RecyclerView.OnScrollListener? = null
    private var listTareas: MutableList<TareaAsignada> = mutableListOf()
    private var listProyectos: MutableList<Proyecto> = mutableListOf()
    private var idProyectoSelected: String? = null
    private var dialogAddTarea: AddTareaDialog? = null

    private lateinit var binding: FragmentTareasAsignadasBinding

    private val viewModel: TareasAsignadasViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            act = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTareasAsignadasBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpObservers()
    }
    
    fun setUpView(){
        binding.apply {
            var layoutManager = LinearLayoutManager(requireContext())
            layoutManager.orientation = RecyclerView.HORIZONTAL
            proyectosRecyclerView.layoutManager = layoutManager
            proyectosRecyclerView.setHasFixedSize(true)
            (proyectosRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            //calcular el padding para centrar los items
            val padding = ConstantHelper.getWidhtScreen(requireActivity()) / 2 - ConstantHelper.dpToPx(requireContext(), 41)

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

            layoutManager = LinearLayoutManager(requireContext())
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

                            dialog.setAddClickListener(this@TareasAsignadasFragment)
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
                    tareasAsignadasRecyclerView.visibility = View.GONE

                    emptyView.tituloEmptyTextView.text = getString(R.string.sin_tareas)
                    emptyView.descripcionEmptyTextView.text = getString(R.string.sin_tareas_desc)

                    Glide.with(requireContext()).load(R.drawable.not_tareas).into(emptyView.emptyImageView)

                } else {

                    emptyViewConstraintLayout.visibility = View.GONE
                    tareasAsignadasRecyclerView.visibility = View.VISIBLE

                    adapterTareas?.setList(lista) ?: run {
                        adapterTareas = AdapterTareasAsignadas(requireContext(), lista) { tarea ->
                            cambiarEstadoTarea(tarea, ConstantHelper.Estados.completada)
                        }
                    }
                    tareasAsignadasRecyclerView.adapter = adapterTareas
                }
            }
        }
    }

    fun cambiarEstadoTarea(tarea: TareaAsignada, estado: ConstantHelper.Estados) {

        binding.dialogObservaciones.apply {

            //mostrar la modal de observaciones
            binding.modalObservacionesTareasAsignadas.visibility = View.VISIBLE
            
            emailTextView.text = getString(R.string.title_observacion)
            descripcionObsercacionTextView.text = getString(R.string.desc_observacion)

            cancelarContraintLayout.setOnClickListener {
                binding.modalObservacionesTareasAsignadas.visibility = View.INVISIBLE
            }

            aceptarContraintLayout.setOnClickListener {
                binding.modalObservacionesTareasAsignadas.visibility = View.INVISIBLE
                tarea.idTarea?.let {
                    viewModel.cambioEstadoTarea(
                        estado.idEstado,
                        tarea.idTarea,
                        observacionesEditText.text.toString()
                    )
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
        binding.tareasAsignadasConstraintLayout.removeView(dialogAddTarea)
        DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
        viewModel.insertarTarea(idProyecto = idProyecto, idEmpleado = idEmpleado, titulo = titulo, descripcion = descripcion, plazo = plazo)
    }

    override fun clickCancelTarea() {
        binding.addTareaFloatingActionButton.visibility = View.VISIBLE
        binding.tareasAsignadasConstraintLayout.removeView(dialogAddTarea)
    }

    //*************************
    //Observers
    //*************************
    fun setUpObservers(){
        viewModel.tareasAsignadasLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(com.toools.ierp.ui.tareasFragment.TAG, "tareas_asignadas: {${response.status}}")
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
                        filterTareas(idProyecto = idProyecto)
                    } ?: run {
                        filterTareas(idProyecto = listProyectos[0].id)
                    }

                }
                Resource.Status.ERROR -> {

                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.tareasError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            viewModel.tareasAsignadas()
                        }
                    )
                }
            }
        }
        viewModel.proyectosLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(com.toools.ierp.ui.tareasFragment.TAG, "proyectos: {${response.status}}")
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
                        viewModel.tareasAsignadas()

                    }

                }
                Resource.Status.ERROR -> {
                    act?.let {
                        DialogHelper.getInstance().showLoadingAlert(it, null, false)
                        DialogHelper.getInstance().showOKAlert(activity = it,
                            title = R.string.ups,
                            text = response.exception ?: ErrorHelper.proyectosError,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {
                                viewModel.proyectos()
                            })
                    }
                }
            }
        }

        viewModel.cambioEstadoTareaLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(com.toools.ierp.ui.tareasFragment.TAG, "proyectos: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)

                    Toasty.success(requireContext(), resources.getString(R.string.cambio_tarea_ok)).show()

                    viewModel.tareasAsignadas()
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
                Log.e(com.toools.ierp.ui.tareasFragment.TAG, "addTarea: {${response.status}}")
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
                            onLoadView()
                        })

                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.insertarTareaError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {

                        })
                }
            }
        }
    }
}