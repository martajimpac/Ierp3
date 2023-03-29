package com.toools.ierp.ui.proyectosFragment

import androidx.fragment.app.Fragment
class ProyectosFragment : Fragment(){

}
/*
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.custom.AddTareaDialog
import com.toools.ierp.custom.AddTareaDialogListener
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.RestBaseObject
import com.toools.ierp.entities.ierp.Proyecto
import com.toools.ierp.entities.ierp.ProyectosResponse
import com.toools.ierp.helpers.DialogHelper
import com.toools.ierp.helpers.rest.ErrorHelper
import kotlinx.android.synthetic.main.dialog_empleados.view.*
import kotlinx.android.synthetic.main.fragment_proyectos.*

const val TAG = "ProyectosFragment"

class ProyectosFragment : Fragment(), ProyectosListener, AddTareaDialogListener {

    private var act: Activity? = null
    private var adapterProyectos: AdapterProyectos? = null
    private var adapterEmpleados: AdapterEmpleados? = null
    private var dialogAddTarea: AddTareaDialog? = null

    private val viewModel: ProyectosViewModel by lazy {
        ViewModelProvider(
            this,
            this.defaultViewModelProviderFactory
        ).get(ProyectosViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.proyectosRecived.observe(this, proyectosObserver)
        viewModel.addTareaRecived.observe(this, insertarTareaObserver)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            act = context
    }

    override fun onResume() {
        super.onResume()
        onLoadView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proyectos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        act?.let {
            val layoutManager = LinearLayoutManager(it)
            layoutManager.orientation = RecyclerView.VERTICAL
            proyectosRecyclerView.layoutManager = layoutManager
            proyectosRecyclerView.setHasFixedSize(true)
            (proyectosRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false

            onLoadView()
        }
    }

    private fun onLoadView() {
        act?.let {
            viewModel.callProyectos()
        }
    }

    //*************************
    //Listener Proyectos
    //*************************
    override fun clickTareas(proyecto: Proyecto, miniaturaImageView: ImageView) {
        val extras = FragmentNavigatorExtras(
            miniaturaImageView to getString(R.string.trans_proyecto, proyecto.id)
        )
        val action =
            ProyectosFragmentDirections.navToTareasFragmentFromProyectosFragment(proyecto.id)
        findNavController().navigate(action, extras)
    }

    override fun clickSoportes(proyecto: Proyecto, miniaturaImageView: ImageView) {
        val extras = FragmentNavigatorExtras(
            miniaturaImageView to getString(R.string.trans_proyecto, proyecto.id)
        )
        val action =
            ProyectosFragmentDirections.navToSoportesFragmentFromProyectosFragment(proyecto.id)
        findNavController().navigate(action, extras)
    }

    var modalEmpleados: View? = null
    override fun clickEmpleados(proyecto: Proyecto) {

        if (modalEmpleados == null) {
            val inflater = LayoutInflater.from(act)
            modalEmpleados =
                inflater.inflate(R.layout.dialog_empleados, proyectosConstraintLayout, false)
        }

        modalEmpleados?.let { modal ->
            proyectosConstraintLayout.addView(modalEmpleados)

            act?.let {
                val gridLayoutManager = GridLayoutManager(it, 3)
                gridLayoutManager.orientation = RecyclerView.VERTICAL
                modal.empleadosRecyclerView.layoutManager = gridLayoutManager
                modal.empleadosRecyclerView.setHasFixedSize(true)
                (modal.empleadosRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                    false

                adapterEmpleados?.let { adapter ->
                    adapter.setList(proyecto.usuarios.toMutableList())
                } ?: run {
                    adapterEmpleados = AdapterEmpleados(it, proyecto.usuarios.toMutableList())
                }

                modal.empleadosRecyclerView.adapter = adapterEmpleados
            }

            modal.tituloProyectoTextView.text = proyecto.nombre

            modal.aceptarContraintLayout.setOnClickListener {
                proyectosConstraintLayout.removeView(modalEmpleados)
            }
        }

    }

    override fun clickAddTarea(proyecto: Proyecto) {

        act?.let {

            if (dialogAddTarea == null) {
                dialogAddTarea = AddTareaDialog(act as Context)
            }

            dialogAddTarea?.let { dialog ->

                dialog.setAddClickListener(this)
                dialog.setProyecto(proyecto)
                dialog.setTitulo(getString(R.string.titulo_add_proyecto, proyecto.nombre))
                proyectosConstraintLayout.addView(dialog)

                val view = dialog.getViewById(R.id.addTareaConstraintLayout)
                val params = view.layoutParams
                params.height = proyectosConstraintLayout.height
                params.width = proyectosConstraintLayout.width
                view.layoutParams = params

            }

        }

    }

    //*************************
    //Listener AddTareaDialog
    //*************************
    override fun clickAddTarea(
        idProyecto: String,
        idEmpleado: String,
        titulo: String,
        descripcion: String,
        plazo: String
    ) {
        proyectosConstraintLayout.removeView(dialogAddTarea)
        act?.let {
            DialogHelper.getInstance().showLoadingAlert(it, null, true)
            viewModel.addTarea(
                idProyecto = idProyecto,
                idEmpleado = idEmpleado,
                titulo = titulo,
                descripcion = descripcion,
                plazo = plazo
            )
        }
    }

    override fun clickCancelTarea() {
        proyectosConstraintLayout.removeView(dialogAddTarea)
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
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)

                    (resource.data?.proyectos?.sortedBy { proyecto -> proyecto.nombre })?.toMutableList()
                        ?.let { listProyectos ->

                            adapterProyectos?.setList(listProyectos) ?: run {
                                adapterProyectos = AdapterProyectos(it, listProyectos, this)
                            }

                            proyectosRecyclerView.adapter = adapterProyectos
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