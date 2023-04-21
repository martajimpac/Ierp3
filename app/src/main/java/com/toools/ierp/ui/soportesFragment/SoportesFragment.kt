package com.toools.ierp.ui.soportesFragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.transition.TransitionInflater
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.core.DialogHelper
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.Proyecto
import com.toools.ierp.data.model.Soporte
import com.toools.ierp.databinding.FragmentSoportesBinding
import com.toools.ierp.ui.adapter.AdapterProyectoGeneral
import com.toools.ierp.ui.main.MainActivity
import com.trinnguyen.SegmentView
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

const val TAG = "SoportesFragment"

@AndroidEntryPoint
class SoportesFragment : Fragment(), SegmentView.OnSegmentItemSelectedListener, SoportesListener {

    private val args: SoportesFragmentArgs by navArgs()

    private var act: Activity? = null
    private var adapterProyectos: AdapterProyectoGeneral? = null
    private var onScrollListener: RecyclerView.OnScrollListener? = null
    private var listProyectos: MutableList<Proyecto> = mutableListOf()
    private var idProyectoSelected: String? = null
    private var tiposSelected: ConstantHelper.Tipos = ConstantHelper.Tipos.todos
    private var listSoportes: MutableList<Soporte> = mutableListOf()
    private var adapterSoportes: AdapterSoportes? = null
    private var soporteSelected: Soporte? = null

    private lateinit var binding: FragmentSoportesBinding

    private val viewModel: SoportesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = context?.let { TransitionInflater.from(it).inflateTransition(android.R.transition.move) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSoportesBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        act?.let {
            (it as MainActivity).showIconBack(false)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.segmentView.onSegmentItemSelectedListener = this;
    }

    override fun onStop() {
        super.onStop()
        binding.segmentView.onSegmentItemSelectedListener = null;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            act = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()

        binding.apply {

            //cargar los datos del SegmentView
            segmentView.setText(ConstantHelper.Tipos.todos.idTipo, getString(R.string.todos))
            segmentView.setText(ConstantHelper.Tipos.propios.idTipo, getString(R.string.propios))
            segmentView.setText(ConstantHelper.Tipos.sinAbrir.idTipo, getString(R.string.sin_abrir))

            var layoutManager = LinearLayoutManager(requireActivity())
            layoutManager.orientation = RecyclerView.HORIZONTAL
            proyectosRecyclerView.layoutManager = layoutManager
            proyectosRecyclerView.setHasFixedSize(true)
            (proyectosRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            //calcular el padding para centrar los items
            // todo solucionar padding
            val padding = ConstantHelper.getWidhtScreen(requireContext()) / 2 - ConstantHelper.dpToPx(requireContext(), 41)
            proyectosRecyclerView.setPadding(padding,0,padding,0)
            proyectosRecyclerView.clipToPadding = false

            LinearSnapHelper().attachToRecyclerView(proyectosRecyclerView)

            onScrollListener = object: RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                    adapterProyectos?.let { adapterProyectos ->
                        val position = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        adapterProyectos.setPositionSelected(position)
                        idProyectoSelected = listProyectos[position].id
                        filterSoportes(idProyectoSelected, tiposSelected)
                    }
                }
            }

            onScrollListener?.let { listener ->
                proyectosRecyclerView.addOnScrollListener(listener)
            }

            layoutManager = LinearLayoutManager(requireContext())
            layoutManager.orientation = RecyclerView.VERTICAL
            soportesRecyclerView.layoutManager = layoutManager
            soportesRecyclerView.setHasFixedSize(true)
            (soportesRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            args.idProyecto?.let{ idProyecto ->
                idProyectoSelected = idProyecto
            } ?: run {
                idProyectoSelected = "-1"
            }

            onLoadView()
        }
    }

    private fun onLoadView(){
        act?.let {
            viewModel.proyectos()
        }
    }

    //*************************
    //Class Metodos
    //*************************
    var emptryView: View? = null
    private fun filterSoportes(idProyecto: String?, tipo: ConstantHelper.Tipos){
        // todo no se que es lo que hace esta funcion
        /*act?.let { context ->

            emptryView?.let {
                emptyViewConstraintLayout.removeView(it)
            }

            listSoportes.filter { soporte -> soporte.idProyecto == idProyecto || idProyecto == "-1" }.toMutableList().let { aux ->

                var lista = mutableListOf<Soporte>()
                if (aux.isEmpty()) {

                    emptyViewConstraintLayout.visibility = View.VISIBLE
                    soportesRecyclerView.visibility = View.GONE

                    if (emptryView == null) {
                        val inflater = LayoutInflater.from(act)
                        emptryView = inflater.inflate(R.layout.empty_view, soportesConstraintLayout, false)
                    }

                    emptryView?.let { emptryView ->
                        emptyViewConstraintLayout.addView(emptryView)

                        emptryView.tituloEmptyTextView.text = getString(R.string.sin_soportes)
                        emptryView.descripcionEmptyTextView.text = getString(R.string.sin_soportes_desc)

                        Glide.with(context).load(R.drawable.not_soportes).into(emptryView.emptyImageView)
                    }

                } else {

                    lista.addAll(aux)

                    //filtrar por el tipo
                    if (tipo != ConstantHelper.Tipos.todos)
                        when (tipo.idTipo){
                            ConstantHelper.Tipos.sinAbrir.idTipo -> {
                                lista = aux.filter { soporte -> soporte.estado == Soporte.sinAsignar }.toMutableList()
                            }
                            ConstantHelper.Tipos.propios.idTipo -> {
                                RestRepository.getInstance().usuario?.userId?.let { userId ->
                                    lista = aux.filter { soporte -> soporte.idUsuario == userId }.toMutableList()
                                }
                            }
                        }

                    emptyViewConstraintLayout.visibility = View.GONE
                    soportesRecyclerView.visibility = View.VISIBLE

                    adapterSoportes?.let {
                        it.setList(lista)
                    } ?: run {
                        adapterSoportes = AdapterSoportes(context, lista, this)
                    }

                    soportesRecyclerView.adapter = adapterSoportes
                }
            }
        }*/
    }

    private fun toRespuestas(soporte: Soporte) {
        val extras = FragmentNavigatorExtras()
        val action = SoportesFragmentDirections.navToRespuestasFragment(soporte)
        findNavController().navigate(action, extras)
    }
    //*************************
    //Listener SegmentView
    //*************************
    override fun onSegmentItemSelected(index: Int) {

        if (index != tiposSelected.idTipo) {
            when (index) {
                ConstantHelper.Tipos.sinAbrir.idTipo -> {
                    tiposSelected = ConstantHelper.Tipos.sinAbrir
                }
                ConstantHelper.Tipos.todos.idTipo -> {
                    tiposSelected = ConstantHelper.Tipos.todos
                }
                ConstantHelper.Tipos.propios.idTipo -> {
                    tiposSelected = ConstantHelper.Tipos.propios
                }
            }

            filterSoportes(idProyectoSelected, tiposSelected)
        }
    }

    override fun onSegmentItemReselected(index: Int) {
        //no hacer nada
    }

    //*************************
    //Listener Soportes
    //*************************
    override fun clickAsignarSoporte(soporte: Soporte) {
        //llamar a asignar soporte
        soporte.idIncidencia?.let { idSoporte ->
            soporteSelected = soporte
            DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
            viewModel.asignarSoportes(idSoporte)
        }
    }

    override fun clickSoporte(soporte: Soporte) {
        //cargar el fragment con las respuestas
        toRespuestas(soporte)
    }

    //*************************
    //Observers
    //*************************
    fun setUpObservers(){
        //proyectos
        viewModel.proyectosLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "proyectos: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                }
                Resource.Status.SUCCESS -> {
                    Log.e(TAG, "${response.data}")

                    (response.data?.proyectos?.sortedBy{ proyecto -> proyecto.nombre })?.toMutableList()?.let { listProyectos ->

                        //TODO NO ENTRTA AQUI porque proyectos es null tendria que decir status ok
                        val todos = Proyecto("-1", "TODOS", null, null, null, "TODOS", mutableListOf())
                        listProyectos.add(0, todos)

                        this.listProyectos = listProyectos

                        adapterProyectos?.setList(listProyectos) ?: run {

                            adapterProyectos = AdapterProyectoGeneral(requireContext(), listProyectos) { position ->
                                binding.proyectosRecyclerView.smoothScrollToPosition(position)
                            }
                        }

                        binding.proyectosRecyclerView.adapter = adapterProyectos

                        viewModel.soportes()
                    }.run{
                        Toasty.warning(requireContext(), "NO TIENES NINGUN PROYECTO", Toast.LENGTH_LONG).show() //TODO
                    }
                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    DialogHelper.getInstance().showOKAlert(
                        activity = requireActivity(),
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.proyectosError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                            viewModel.proyectos()
                        })
                }
            }
        }
        //soportes
        viewModel.soportesLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "soportes: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)

                    listSoportes = response.data?.soportes?.let{ lista ->
                        lista.toMutableList()
                    } ?: run { mutableListOf<Soporte>()}

                    idProyectoSelected?.let{ idProyecto ->

                        val index = listProyectos.indexOfFirst { proyecto -> proyecto.id == idProyecto }
                        binding.proyectosRecyclerView.scrollToPosition(index)
                        adapterProyectos?.let { adapterProyectos ->
                            adapterProyectos.setPositionSelected(index)
                        }
                        filterSoportes(idProyectoSelected, tiposSelected)
                    } ?: run {
                        filterSoportes(idProyectoSelected, tiposSelected)
                    }

                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    DialogHelper.getInstance().showOKAlert(
                        activity = requireActivity(),
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.soportesError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                            viewModel.soportes()
                        }
                    )
                }
            }
        }
        //asignarSoportes
        viewModel.asignarSoportesLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "asignarSoporte: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    Toasty.success(requireContext(),getString(R.string.soporte_asignado)).show()
                    //toRestpuestas
                    soporteSelected?.let { soporte ->
                        toRespuestas(soporte)
                    }
                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    DialogHelper.getInstance().showOKAlert(
                        activity = requireActivity(),
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.asignarSoporteError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            soporteSelected?.idIncidencia?.let { idSoporte ->
                                DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                                viewModel.asignarSoportes(idSoporte)
                            }
                        }
                    )
                }
            }
        }
    }
}