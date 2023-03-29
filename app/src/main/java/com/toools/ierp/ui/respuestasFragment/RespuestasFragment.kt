package com.toools.ierp.ui.respuestasFragment

import androidx.fragment.app.Fragment
class RespuestasFragment : Fragment(){ }

/*
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.RestBaseObject
import com.toools.ierp.entities.ierp.RespuestasResponse
import com.toools.ierp.entities.ierp.Soporte
import com.toools.ierp.helpers.DialogHelper
import com.toools.ierp.helpers.rest.ErrorHelper
import com.toools.ierp.ui.main.MainActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialog_observaciones.view.*
import kotlinx.android.synthetic.main.fragment_respuestas.*

const val TAG = "RespuestasFragment"

class RespuestasFragment : Fragment() {

    private val args: RespuestasFragmentArgs by navArgs()

    private var act: Activity? = null
    private var soporte: Soporte? = null
    private var adapterRespuestas: AdapterRespuestas? = null

    private val viewModel: RespuestasViewModel by lazy {
        ViewModelProvider(this, this.defaultViewModelProviderFactory)[RespuestasViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            act = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
        viewModel.respuestasRecived.observe(this, respuestasObserver)
        viewModel.asignarSoportesRecived.observe(this, asignarSoporteObserver)
        viewModel.cerrarSoportesRecived.observe(this, cerrarSoporteObserver)
        viewModel.nuevaRespuestaRecived.observe(this, nuevaRespuestaObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_respuestas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        act?.let {

            Glide.with(it).load(args.soporte.imgMiniProyecto).circleCrop().error(Glide.with(it).load(R.drawable.ic_proyectos).circleCrop()).into(proyectoImageView)
            Glide.with(it).load(args.soporte.imagenAsignado).circleCrop().error(Glide.with(it).load(R.drawable.luciano).circleCrop()).into(asignadoImageView)

            tituloTextView.text = args.soporte.titulo
            descripcionTextView.text = Html.fromHtml(args.soporte.descripcion, Html.FROM_HTML_MODE_LEGACY)

            soporte = args.soporte

            val layoutManager = LinearLayoutManager(it)
            layoutManager.orientation = RecyclerView.VERTICAL
            respuestasRecyclerView.layoutManager = layoutManager
            respuestasRecyclerView.setHasFixedSize(true)
            (respuestasRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            onLoadView()
        }
    }

    override fun onResume() {
        super.onResume()
        act?.let {
            (it as MainActivity).showIconBack(true)
        }
    }

    private fun onLoadView(){
        act?.let {act ->

            if (args.soporte.estado == Soporte.sinAsignar) {
                leftBtnCardView.setCardBackgroundColor(act.getColor(R.color.yellow_app))
                leftBtnTextView.text = getString(R.string.asignarme_el_soporte)
                leftBtnCardView.setOnClickListener {
                    args.soporte.idIncidencia?.let { idSoporte ->
                        DialogHelper.getInstance().showLoadingAlert(act, null, true)
                        viewModel.asignarSoportes(idSoporte)
                    }
                }
            } else {
                leftBtnCardView.setCardBackgroundColor(act.getColor(R.color.colorPrimary))
                leftBtnTextView.text = getString(R.string.nueva_respuesta)
                leftBtnCardView.setOnClickListener {
                    args.soporte.idIncidencia?.let { idSoporte ->
                        nuevaRespuesta(idSoporte)
                    }
                }
            }

            rightBtnCardView.setOnClickListener {
                args.soporte.idIncidencia?.let { idSoporte ->
                    DialogHelper.getInstance().showLoadingAlert(act, null, true)
                    viewModel.cerrarSoportes(idSoporte)
                }
            }

            DialogHelper.getInstance().showLoadingAlert(act, null, true)
            args.soporte.idIncidencia?.let{ idSoporte ->
                DialogHelper.getInstance().showLoadingAlert(act, null, true)
                viewModel.callRespuestas(idSoporte)
            }
        }
    }

    fun nuevaRespuesta(idSoporte: String) {
        act?.let { act ->
            //mostrar la modal de observaciones
            val inflater = LayoutInflater.from(act)
            val modalObservaciones =
                inflater.inflate(R.layout.dialog_observaciones, respuestasConstraintLayout, false)

            respuestasConstraintLayout.addView(modalObservaciones)

            modalObservaciones.emailTextView.text = getString(R.string.nueva_respuesta)
            modalObservaciones.descripcionObsercacionTextView.text = getString(R.string.desc_respuesta)
            modalObservaciones.observacionesEditText.hint = getString(R.string.texto_respuesta)

            modalObservaciones.cancelarContraintLayout.setOnClickListener {
                respuestasConstraintLayout.removeView(modalObservaciones)
            }

            modalObservaciones.aceptarContraintLayout.setOnClickListener {
                respuestasConstraintLayout.removeView(modalObservaciones)
                DialogHelper.getInstance().showLoadingAlert(act, null, true)
                viewModel.sendNuevaRespuesta(
                    idSoporte,
                    modalObservaciones.observacionesEditText.text.toString()
                )
            }
        }
    }

    //*************************
    //Observers
    //*************************
    private val respuestasObserver = Observer<Resource<RespuestasResponse>> { resource ->

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

                    resource.data?.respuestas?.toMutableList()?.let { list ->
                        adapterRespuestas?.let { adapter ->
                            adapter.setList(list)
                        } ?: run {
                            adapterRespuestas = AdapterRespuestas(it, list)
                        }

                        respuestasRecyclerView.adapter = adapterRespuestas
                    }
                }
            }
            Resource.Status.ERROR -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)
                    DialogHelper.getInstance().showOKAlert(
                        activity = it,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.respuestasError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            args.soporte.idIncidencia?.let{ idSoporte ->
                                viewModel.callRespuestas(idSoporte)
                            }
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
                    args.soporte.estado = "1"
                    onLoadView()
                }
            }
            Resource.Status.ERROR -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = it,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.asignarSoporteError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            args.soporte.idIncidencia?.let { idSoporte ->
                                DialogHelper.getInstance().showLoadingAlert(it, null, true)
                                viewModel.asignarSoportes(idSoporte)
                            }
                        })
                }
            }
        }
    }

    private val cerrarSoporteObserver = Observer<Resource<RestBaseObject>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(TAG, "cerrarSoporte: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, true)
                }

            }
            Resource.Status.SUCCESS -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)

                    Toasty.success(it,getString(R.string.cerrar_soporte)).show()
                    findNavController().popBackStack()
                }
            }
            Resource.Status.ERROR -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = it,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.cerrarSoporteError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            args.soporte.idIncidencia?.let { idSoporte ->
                                DialogHelper.getInstance().showLoadingAlert(it, null, true)
                                viewModel.cerrarSoportes(idSoporte)
                            }
                        })
                }
            }
        }
    }

    private val nuevaRespuestaObserver = Observer<Resource<RestBaseObject>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(TAG, "nuevaRespuesta: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, true)
                }

            }
            Resource.Status.SUCCESS -> {
                act?.let {
                    Toasty.success(it,getString(R.string.send_repuesta_ok)).show()
                    args.soporte.idIncidencia?.let { idSoporte ->
                        viewModel.callRespuestas(idSoporte)
                    }
                }
            }
            Resource.Status.ERROR -> {
                act?.let {
                    DialogHelper.getInstance().showLoadingAlert(it, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = it,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.sendRespuestaError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {

                        })
                }
            }
        }
    }
}*/