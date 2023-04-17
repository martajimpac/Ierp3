package com.toools.ierp.ui.proyectosFragment

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.Gson
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.core.*
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.LoginResponse
import com.toools.ierp.data.model.Proyecto
import com.toools.ierp.databinding.FragmentProyectosBinding
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "ProyectosFragment"
@AndroidEntryPoint
class ProyectosFragment : Fragment(), ProyectosListener, AddTareaDialogListener {

    private var adapterProyectos: AdapterProyectos? = null
    private var adapterEmpleados: AdapterEmpleados? = null
    private var dialogAddTarea: AddTareaDialog? = null
    private lateinit var binding: FragmentProyectosBinding
    private var usuario: LoginResponse?=null

    private val viewModel: ProyectosViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProyectosBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        onLoadView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //ocultar la vista
        binding.dialogEmpleados.containerDialogEmpleados.visibility = View.GONE

        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireActivity())
        layoutManager.orientation = RecyclerView.VERTICAL

        binding.proyectosRecyclerView.layoutManager = layoutManager
        binding.proyectosRecyclerView.setHasFixedSize(true)
        (binding.proyectosRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        usuario?: Gson().fromJson(
            requireContext().prefs.getString(ConstantHelper.usuarioLogin, null),
            LoginResponse::class.java
        )
        setUpObservers()
    }

    private fun onLoadView() {
        usuario?: Gson().fromJson(
            requireContext().prefs.getString(ConstantHelper.usuarioLogin, null),
            LoginResponse::class.java
        )
        usuario?.let{
            viewModel.proyectos(usuario?.token!!)
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

    override fun clickEmpleados(proyecto: Proyecto) {
        binding.dialogEmpleados.apply{

            val gridLayoutManager = GridLayoutManager(requireActivity(), 3)
            gridLayoutManager.orientation = RecyclerView.VERTICAL
            empleadosRecyclerView.layoutManager = gridLayoutManager
            empleadosRecyclerView.setHasFixedSize(true)
            (empleadosRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            adapterEmpleados?.let { adapter ->
                adapter.setList(proyecto.usuarios.toMutableList())
            } ?: run {
                adapterEmpleados = AdapterEmpleados(requireActivity(), proyecto.usuarios.toMutableList())
            }

            empleadosRecyclerView.adapter = adapterEmpleados

            tituloProyectoTextView.text = proyecto.nombre

            //ocultar vista
            aceptarContraintLayout.setOnClickListener {
                containerDialogEmpleados.visibility = View.GONE
            }
        }
    }
    override fun clickAddTarea(proyecto: Proyecto) {

        if (dialogAddTarea == null) {
            dialogAddTarea = AddTareaDialog(requireContext())
        }
        dialogAddTarea?.let { dialog ->

            binding.apply{
                dialog.setAddClickListener(this@ProyectosFragment)
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
        binding.proyectosConstraintLayout.removeView(dialogAddTarea)
        DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
        usuario?.let{
            viewModel.insertarTarea(
                token = usuario?.token!!,
                idProyecto = idProyecto,
                idEmpleado = idEmpleado,
                titulo = titulo,
                descripcion = descripcion,
                plazo = plazo
            )
        }
   }

    override fun clickCancelTarea() {
        //proyectosConstraintLayout.removeView(dialogAddTarea)
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

                    (response.data?.proyectos?.sortedBy { proyecto -> proyecto.nombre })?.toMutableList()
                        ?.let { listProyectos ->

                            adapterProyectos?.setList(listProyectos) ?: run {
                                adapterProyectos = AdapterProyectos(requireActivity(), listProyectos, this)
                            }

                            binding.proyectosRecyclerView.adapter = adapterProyectos
                        }
                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.proyectosError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            viewModel.proyectos(usuario?.token!!)
                        })
                }
            }
        }

        viewModel.proyectosLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "addTarea: {${response.status}}")
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
                        completion = {

                        })
                }
            }
        }
    }
}