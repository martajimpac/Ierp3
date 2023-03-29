package com.toools.ierp.ui.soportesFragment

import androidx.fragment.app.Fragment
class SoportesFragment : Fragment(){ }
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
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.RestBaseObject
import com.toools.ierp.entities.ierp.Proyecto
import com.toools.ierp.entities.ierp.ProyectosResponse
import com.toools.ierp.entities.ierp.Soporte
import com.toools.ierp.entities.ierp.SoportesResponse
import com.toools.ierp.helpers.ConstantsHelper
import com.toools.ierp.helpers.DialogHelper
import com.toools.ierp.helpers.rest.ErrorHelper
import com.toools.ierp.helpers.rest.RestRepository
import com.toools.ierp.ui.adapter.AdapterProyectoGeneral
import com.toools.ierp.ui.main.MainActivity
import com.trinnguyen.SegmentView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.empty_view.view.*
import kotlinx.android.synthetic.main.fragment_soportes.*

const val TAG = "SoportesFragment"

class SoportesFragment : Fragment(), SegmentView.OnSegmentItemSelectedListener, SoportesListener {

    private val args: SoportesFragmentArgs by navArgs()

    private var act: Activity? = null
    private var adapterProyectos: AdapterProyectoGeneral? = null
    private var onScrollListener: RecyclerView.OnScrollListener? = null
    private var listProyectos: MutableList<Proyecto> = mutableListOf()
    private var idProyectoSelected: String? = null
    private var tiposSelected: ConstantsHelper.Tipos = ConstantsHelper.Tipos.todos
    private var listSoportes: MutableList<Soporte> = mutableListOf()
    private var adapterSoportes: AdapterSoportes? = null
    private var soporteSelected: Soporte? = null

    private val viewModel: SoportesViewModel by lazy {
        ViewModelProvider(this, this.defaultViewModelProviderFactory)[SoportesViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
        viewModel.proyectosRecived.observe(this, proyectosObserver)
        viewModel.soportesRecived.observe(this, soportesObserver)
        viewModel.asignarSoportesRecived.observe(this, asignarSoporteObserver)

        sharedElementEnterTransition = context?.let { TransitionInflater.from(it).inflateTransition(android.R.transition.move) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            act = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_soportes, container, false)
    }

    override fun onResume() {
        super.onResume()
        act?.let {
            (it as MainActivity).showIconBack(false)
        }
    }

    override fun onStart() {
        super.onStart()
        segmentView.onSegmentItemSelectedListener = this;
    }

    override fun onStop() {
        super.onStop()
        segmentView.onSegmentItemSelectedListener = null;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        act?.let {

            //cargar los datos del SegmentView
            segmentView.setText(ConstantsHelper.Tipos.todos.idTipo, getString(R.string.todos))
            segmentView.setText(ConstantsHelper.Tipos.propios.idTipo, getString(R.string.propios))
            segmentView.setText(ConstantsHelper.Tipos.sinAbrir.idTipo, getString(R.string.sin_abrir))

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
                        filterSoportes(idProyectoSelected, tiposSelected)
                    }
                }
            }

            onScrollListener?.let { listener ->
                proyectosRecyclerView.addOnScrollListener(listener)
            }

            layoutManager = LinearLayoutManager(it)
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
//            DialogHelper.getInstance().showLoadingAlert(it, null, true)
            viewModel.callProyectos()
        }
    }

    //*************************
    //Class Metodos
    //*************************
    var emptryView: View? = null
    private fun filterSoportes(idProyecto: String?, tipo: ConstantsHelper.Tipos){
        act?.let { context ->

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
                    if (tipo != ConstantsHelper.Tipos.todos)
                        when (tipo.idTipo){
                            ConstantsHelper.Tipos.sinAbrir.idTipo -> {
                                lista = aux.filter { soporte -> soporte.estado == Soporte.sinAsignar }.toMutableList()
                            }
                            ConstantsHelper.Tipos.propios.idTipo -> {
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
        }
    }

    private fun toRespuestas(soporte: Soporte) {
        val extras = FragmentNavigatorExtras(

        )
        val action = SoportesFragmentDirections.navToRespuestasFragment(soporte)
        findNavController().navigate(action, extras)
    }
    //*************************
    //Listener SegmentView
    //*************************
    override fun onSegmentItemSelected(index: Int) {

        if (index != tiposSelected.idTipo) {
            when (index) {
                ConstantsHelper.Tipos.sinAbrir.idTipo -> {
                    tiposSelected = ConstantsHelper.Tipos.sinAbrir
                }
                ConstantsHelper.Tipos.todos.idTipo -> {
                    tiposSelected = ConstantsHelper.Tipos.todos
                }
                ConstantsHelper.Tipos.propios.idTipo -> {
                    tiposSelected = ConstantsHelper.Tipos.propios
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
        act?.let {
            //llamar a asignar soporte
            soporte.idIncidencia?.let { idSoporte ->
                soporteSelected = soporte
                DialogHelper.getInstance().showLoadingAlert(it, null, true)
                viewModel.callAsignarSoportes(idSoporte)
            }
        }
    }

    override fun clickSoporte(soporte: Soporte) {
        //cargar el fragment con las respuestas
        toRespuestas(soporte)
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

                        viewModel.callSoportes()

                    }
                }
            }
            Resource.Status.ERROR -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)
                    DialogHelper.getInstance().showOKAlert(
                        activity = it,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.proyectosError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            DialogHelper.getInstance().showLoadingAlert(it, null, true)
                            viewModel.callProyectos()
                        })
                }
            }
        }
    }

    private val soportesObserver = Observer<Resource<SoportesResponse>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(TAG, "soportes: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, true)
                }

            }
            Resource.Status.SUCCESS -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)

                    listSoportes = resource.data?.soportes?.let{ lista ->
                        lista.toMutableList()
                    } ?: run { mutableListOf<Soporte>()}

                    idProyectoSelected?.let{ idProyecto ->

                        val index = listProyectos.indexOfFirst { proyecto -> proyecto.id == idProyecto }
                        proyectosRecyclerView.scrollToPosition(index)
                        adapterProyectos?.let { adapterProyectos ->
                            adapterProyectos.setPositionSelected(index)
                        }
                        filterSoportes(idProyectoSelected, tiposSelected)
                    } ?: run {
                        filterSoportes(idProyectoSelected, tiposSelected)
                    }
                }
            }
            Resource.Status.ERROR -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)
                    DialogHelper.getInstance().showOKAlert(
                        activity = it,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.soportesError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            DialogHelper.getInstance().showLoadingAlert(it, null, true)
                            viewModel.callSoportes()
                        })
                }
            }
        }
    }

    private val asignarSoporteObserver = Observer<Resource<RestBaseObject>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(TAG, "asignarSoporte: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, true)
                }

            }
            Resource.Status.SUCCESS -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)

                    Toasty.success(it,getString(R.string.soporte_asignado)).show()
                    //toRestpuestas
                    soporteSelected?.let { soporte ->
                        toRespuestas(soporte)
                    }
                }
            }
            Resource.Status.ERROR -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)
                    DialogHelper.getInstance().showOKAlert(
                        activity = it,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.asignarSoporteError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            soporteSelected?.idIncidencia?.let { idSoporte ->
                                DialogHelper.getInstance().showLoadingAlert(it, null, true)
                                viewModel.callAsignarSoportes(idSoporte)
                            }
                        })
                }
            }
        }
    }
}*/