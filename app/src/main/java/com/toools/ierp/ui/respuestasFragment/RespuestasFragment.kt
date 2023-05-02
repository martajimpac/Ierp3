package com.toools.ierp.ui.respuestasFragment

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.data.model.Soporte
import com.toools.ierp.databinding.FragmentRespuestasBinding
import com.toools.ierp.ui.main.MainActivity
import com.toools.tooolsdialog.DialogHelper
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty


const val TAG = "RespuestasFragment"

@AndroidEntryPoint
class RespuestasFragment : Fragment() {

    private val args: RespuestasFragmentArgs by navArgs()

    private var soporte: Soporte? = null
    private var adapterRespuestas: AdapterRespuestas? = null

    private lateinit var binding: FragmentRespuestasBinding

    private val viewModel: RespuestasViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRespuestasBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply{
            Glide.with(requireContext()).load(args.soporte.imgMiniProyecto).circleCrop().error(Glide.with(requireContext()).load(R.drawable.ic_proyectos).circleCrop()).into(proyectoImageView)
            Glide.with(requireContext()).load(args.soporte.imagenAsignado).circleCrop().error(Glide.with(requireContext()).load(R.drawable.luciano).circleCrop()).into(asignadoImageView)

            tituloTextView.text = args.soporte.titulo
            descripcionTextView.text = Html.fromHtml(args.soporte.descripcion, Html.FROM_HTML_MODE_LEGACY)

            soporte = args.soporte

            val layoutManager = LinearLayoutManager(requireActivity())
            layoutManager.orientation = RecyclerView.VERTICAL
            respuestasRecyclerView.layoutManager = layoutManager
            respuestasRecyclerView.setHasFixedSize(true)
            (respuestasRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            onLoadView()
            setUpObservers()
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            (it as MainActivity).showIconBack(true)
        }
    }

    private fun onLoadView(){
        binding.apply {
            if (args.soporte.estado == Soporte.sinAsignar) {
                leftBtnCardView.setCardBackgroundColor(requireActivity().getColor(R.color.yellow_app))
                leftBtnTextView.text = getString(R.string.asignarme_el_soporte)
                leftBtnCardView.setOnClickListener {
                    args.soporte.idIncidencia?.let { idSoporte ->
                        DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)
                        viewModel.asignarSoportes(idSoporte)
                    }
                }
            } else {
                leftBtnCardView.setCardBackgroundColor(requireActivity().getColor(R.color.colorPrimary))
                leftBtnTextView.text = getString(R.string.nueva_respuesta)
                leftBtnCardView.setOnClickListener {
                    args.soporte.idIncidencia?.let { idSoporte ->
                        nuevaRespuesta(idSoporte)
                    }
                }
            }

            rightBtnCardView.setOnClickListener {
                args.soporte.idIncidencia?.let { idSoporte ->
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)
                    viewModel.cerrarSoporte(idSoporte)
                }
            }

            DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)
            args.soporte.idIncidencia?.let{ idSoporte ->
                DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)
                viewModel.respuestas(idSoporte)
            }
        }
    }

    fun nuevaRespuesta(idSoporte: String) {

        binding.dialogObservaciones.apply {
            //mostrar la modal de observaciones

            binding.modalObservacionesRespuestas.visibility = View.VISIBLE

            emailTextView.text = getString(R.string.nueva_respuesta)
            descripcionObsercacionTextView.text = getString(R.string.desc_respuesta)
            observacionesEditText.hint = getString(R.string.texto_respuesta)

           cancelarContraintLayout.setOnClickListener {
               binding.modalObservacionesRespuestas.visibility = View.GONE
           }

            aceptarContraintLayout.setOnClickListener {
                binding.modalObservacionesRespuestas.visibility = View.GONE
                DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)
                viewModel.sendRespuesta(
                    idSoporte,
                    observacionesEditText.text.toString()
                )
            }
        }
    }

    //*************************
    //Observers
    //*************************

    fun setUpObservers() {
        viewModel.respuestasLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "respuestas: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, false)

                    response.data?.respuestas?.toMutableList()?.let { list ->
                        adapterRespuestas?.setList(list) ?: run {
                            adapterRespuestas = AdapterRespuestas(requireContext(), list)
                        }
                        binding.respuestasRecyclerView.adapter = adapterRespuestas
                    }
                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, false)
                    DialogHelper.getInstance().showOKAlert(
                        activity = requireActivity() as AppCompatActivity,
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.respuestasError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            args.soporte.idIncidencia?.let { idSoporte ->
                                viewModel.respuestas(idSoporte)
                            }
                        }
                    )
                }
            }
        }
        viewModel.asignarSoportesLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "asignarSoporte: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, false)
                    Toasty.success(requireContext(),getString(R.string.soporte_asignado)).show()
                    args.soporte.estado = "1"
                    onLoadView()
                }
                Resource.Status.ERROR -> {
                     DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, false)
                     DialogHelper.getInstance().showOKAlert(activity = requireActivity() as AppCompatActivity,
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.asignarSoporteError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            args.soporte.idIncidencia?.let { idSoporte ->
                                DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)
                                viewModel.asignarSoportes(idSoporte)
                            }
                        }
                    )
                }
            }
        }
        viewModel.cerrarSoporteLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "cerrarSoporte: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, false)
                    Toasty.success(requireContext(),getString(R.string.cerrar_soporte)).show()
                    findNavController().popBackStack()
                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = requireActivity() as AppCompatActivity,
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.cerrarSoporteError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            args.soporte.idIncidencia?.let { idSoporte ->
                                DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)
                                viewModel.cerrarSoporte(idSoporte)
                            }
                        }
                    )

                }
            }
        }
        viewModel.sendRespuestaLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "sendRespuesta: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)
                }
                Resource.Status.SUCCESS -> {
                    Toasty.success(requireContext(),getString(R.string.send_repuesta_ok)).show()
                    args.soporte.idIncidencia?.let { idSoporte ->
                        viewModel.respuestas(idSoporte)
                    }
                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = requireActivity() as AppCompatActivity,
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.sendRespuestaError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {})
                }
            }
        }
    }
}