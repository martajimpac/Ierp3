package com.toools.ierp.ui.guardiasFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.core.EditTextDialog
import com.toools.ierp.core.EditTextDialogListener
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.data.Repository
import com.toools.ierp.data.model.GuardiasResponse
import com.toools.ierp.data.model.LoginResponse
import com.toools.ierp.databinding.FragmentGuardiasBinding
import com.toools.ierp.ui.base.BaseFragment
import com.toools.ierp.ui.main.MainActivity
import com.toools.ierp.ui.login.TAG
import com.toools.tooolsdialog.DialogHelper
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class GuardiasFragment : BaseFragment() {

    private var act: Activity? = null
    private lateinit var binding: FragmentGuardiasBinding
    var usuario : LoginResponse? = null

    var dia : Int = 1
    var mes : Int = 7
    var anyo : Int = 2019

    private val viewModel: GuardiasViewModel by viewModels()

    var  formatFecha = SimpleDateFormat("dd' de 'MMMM' de 'yyyy", Locale.getDefault())
    private var adapter : AdapterGuardias? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGuardiasBinding.inflate(inflater, container,false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            GuardiasFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onResume() {
        super.onResume()
        onLoadView()
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        setUpView()
    }

    fun setUpView(){
        binding.apply {

            onLoadView()

            //comprobar si existen acciones y cargar el recicler.
            val layoutManager = LinearLayoutManager(requireContext())
            layoutManager.orientation = RecyclerView.VERTICAL
            recyclerGuardias.layoutManager = layoutManager
            recyclerGuardias.setHasFixedSize(true)
            val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            recyclerGuardias.addItemDecoration(decoration)
            (recyclerGuardias.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false

            contentCalendar.setOnClickListener {

                DatePickerDialog(requireContext(), R.style.DatePickerDialogTheme,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

                        anyo = year
                        mes = month + 1
                        dia = dayOfMonth

                        DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)

                        usuario?.token?.let {
                            viewModel.guardias(it, dia, mes, anyo)
                        } ?: run {
                            viewModel.guardias("", dia, mes, anyo)
                        }

                    }, anyo, mes - 1, dia
                ).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onLoadView(){

        val cal = Calendar.getInstance()
        anyo = cal.get(Calendar.YEAR)
        mes = cal.get(Calendar.MONTH) + 1
        dia = cal.get(Calendar.DAY_OF_MONTH)

        usuario = Repository.usuario

        if (usuario != null) {
            viewModel.guardias(usuario!!.token!!, dia, mes, anyo)
        }else{
            if(act != null)
                (act as MainActivity).onBackToLogin()
        }
    }

    private fun cargarGuardias(guardias: MutableList<GuardiasResponse.Guardias>, zonaHoraria: String){

        binding.apply{
            var isEntrar = true

            val cal = Calendar.getInstance()
            cal.set(anyo, mes-1, dia)
            txtFechaGuar.text = formatFecha.format(cal.time)

            if(guardias.isNullOrEmpty() || guardias.size == 0){

                txtTituGuardias.text = resources.getString(R.string.sin_guardias)

                txtDescGuardias.text = resources.getString(R.string.not_guardias)

                recyclerGuardias.visibility = View.GONE

            }else{

                txtTituGuardias.text = resources.getString(R.string.entra_sal)

                txtDescGuardias.text = resources.getString(R.string.txt_horas, zonaHoraria)

                recyclerGuardias.visibility = View.VISIBLE

                //cargar el recycler
                adapter?.let{
                    adapter!!.setList(guardias)
                }.run {
                    adapter = AdapterGuardias(requireContext(), guardias)
                }

                recyclerGuardias.adapter = adapter

                if(guardias.last().tipo == GuardiasResponse.Guardias.entrada){
                    isEntrar = false
                }
            }

            if(isEntrar){
                Glide.with(this@GuardiasFragment).load(resources.getDrawable(R.drawable.swords, null)).into(imgAccionGuar)
                txtBtnAccionGuar.text = resources.getString(R.string.guar_entrada)
            }else{
                Glide.with(this@GuardiasFragment).load(resources.getDrawable(R.drawable.shield, null)).into(imgAccionGuar)
                txtBtnAccionGuar.text = resources.getString(R.string.guar_salida)
            }

            contentBtnGuar.setOnClickListener {

                if(isEntrar){
                    DialogHelper.getInstance().showTwoButtonsAlert(
                        requireActivity() as AppCompatActivity,
                        resources.getString(R.string.guardias),
                        resources.getString(R.string.warn_guardias),
                        R.drawable.ic_toools_rellena,
                        resources.getString(R.string.yes),
                        completion1 = {
                            EditTextDialog.getInstance().showEditTextAlert(
                                requireActivity(),
                                resources.getString(R.string.descripcion),
                                resources.getString(R.string.desc_aler_desc),
                                R.drawable.ic_toools_rellena,
                                resources.getString(R.string.ok),
                                object : EditTextDialogListener {
                                    override fun editTextDialogDismissed(value: String) {
                                        usuario?.token?.let {
                                            viewModel.addGuardias(it , Calendar.getInstance().time, value, GuardiasResponse.Guardias.entradaInt )
                                        } ?: run{
                                            viewModel.addGuardias("", Calendar.getInstance().time, value, GuardiasResponse.Guardias.entradaInt)
                                        }
                                    }
                                },
                                button2 = resources.getString(R.string.cancel),
                                hint = resources.getString(R.string.descripcion)
                            )
                        },
                        button2 = resources.getString(R.string.no),
                        completion2 = {}
                    )
                }else{
                    usuario?.token?.let {
                        viewModel.addGuardias(it ,Calendar.getInstance().time, "", GuardiasResponse.Guardias.salidaInt)
                    } ?: run{
                        viewModel.addGuardias("",Calendar.getInstance().time, "", GuardiasResponse.Guardias.salidaInt)
                    }
                }
            }
        }

    }

    //*************************
    //Observers
    //*************************

    fun setUpObservers(){

        //guardias
        viewModel.guardiasLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "guardias: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)

                }
                Resource.Status.SUCCESS -> {

                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, false)
                    if (response.data?.error != null && Integer.parseInt(response.data.error) == ErrorHelper.SESSION_EXPIRED) {
                        DialogHelper.getInstance().showOKAlert(activity = requireActivity() as AppCompatActivity,
                            title = R.string.not_session,
                            text = R.string.desc_not_session,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {
                                if(act != null)
                                    (act as MainActivity).onBackToLogin()
                            })

                    }else {
                        Repository.usuario?.zonaHoraria = response.data!!.timeZone!!
                        cargarGuardias(response.data.actuaciones, response.data.timeZone!!)
                    }

                }
                Resource.Status.ERROR -> {

                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = requireActivity() as AppCompatActivity,
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.momentosError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            usuario?.token?.let {
                                viewModel.guardias(it , dia, mes, anyo)
                            } ?: run{
                                viewModel.guardias("" , dia, mes, anyo)
                            }
                        })

                }
            }
        }

        //addGuardias
        viewModel.addGuardiasLiveData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "addGuardias: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, false)

                    if (response.data?.error != null && Integer.parseInt(response.data.error) == ErrorHelper.SESSION_EXPIRED) {
                        DialogHelper.getInstance().showOKAlert(activity = requireActivity() as AppCompatActivity,
                            title = R.string.not_session,
                            text = R.string.desc_not_session,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {
                                if(act != null)
                                    (act as MainActivity).onBackToLogin()
                            })

                    }else {
                        Repository.usuario!!.zonaHoraria = response.data!!.timeZone!!
                        cargarGuardias(response.data.actuaciones, response.data.timeZone!!)

                        DialogHelper.getInstance().showOKAlert(activity = requireActivity() as AppCompatActivity,
                            title = R.string.guardias,
                            text = R.string.guardias_correcta,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {
                            })
                    }

                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() as AppCompatActivity, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = requireActivity() as AppCompatActivity,
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.momentosError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {

                        })
                }
            }
        }
    }
}