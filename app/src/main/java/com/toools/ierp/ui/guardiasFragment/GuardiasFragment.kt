package com.toools.ierp.ui.guardiasFragment

/*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.ierp.GuardiasResponse
import com.toools.ierp.entities.ierp.LoginResponse
import com.toools.ierp.helpers.DialogHelper
import com.toools.ierp.helpers.EditTextDialogListener
import com.toools.ierp.helpers.rest.ErrorHelper
import com.toools.ierp.helpers.rest.RestRepository
import com.toools.ierp.ui.base.BaseFragment
import com.toools.ierp.ui.main.MainActivity
import com.toools.ierp.ui.login.TAG
import kotlinx.android.synthetic.main.fragment_guardias.*
import kotlinx.android.synthetic.main.fragment_guardias.contentCalendar
import java.text.SimpleDateFormat
import java.util.*

class GuardiasFragment : BaseFragment() {

    private var act: Activity? = null

    var usuario : LoginResponse? = null

    var dia : Int = 1
    var mes : Int = 7
    var anyo : Int = 2019

    var  formatFecha = SimpleDateFormat("dd' de 'MMMM' de 'yyyy", Locale.getDefault())
    private var adapter : AdapterGuardias? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guardias, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            act = context
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

    private val viewModel: GuardiasViewModel by lazy {

        ViewModelProvider(this, this.defaultViewModelProviderFactory).get(GuardiasViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.guardiasRecived.observe(this, guardiasObservable)
        viewModel.addGuardiasRecived.observe(this, addGuardiasObservable)
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        act?.let { act->

            onLoadView()

            //comprobar si existen acciones y cargar el recicler.
            val layoutManager = LinearLayoutManager(act)
            layoutManager.orientation = RecyclerView.VERTICAL
            recyclerGuardias.layoutManager = layoutManager
            recyclerGuardias.setHasFixedSize(true)
            val decoration = DividerItemDecoration(act, DividerItemDecoration.VERTICAL)
            recyclerGuardias.addItemDecoration(decoration)
            (recyclerGuardias.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false

            contentCalendar.setOnClickListener {

                DatePickerDialog(act as Context, R.style.DatePickerDialogTheme,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

                        anyo = year
                        mes = month + 1
                        dia = dayOfMonth

                        DialogHelper.getInstance().showLoadingAlert(act!!, null, true)

                        usuario?.token?.let {
                            viewModel.callGuardias(it, dia, mes, anyo)
                        } ?: run {
                            viewModel.callGuardias("", dia, mes, anyo)
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

        usuario = RestRepository.getInstance().usuario

        if (usuario != null) {
            viewModel.callGuardias(usuario!!.token!!, dia, mes, anyo)
        }else{
            if(act != null)
                (act as MainActivity).onBackToLogin()
        }
    }

    private fun cargarGuardias(guardias: MutableList<GuardiasResponse.Guardias>, zonaHoraria: String){

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
            if(adapter!=null){
                adapter!!.setList(guardias)
            }else{
                adapter = AdapterGuardias(act as Context, guardias)
            }

            recyclerGuardias.adapter = adapter

            if(guardias.last().tipo == GuardiasResponse.Guardias.entrada){
                isEntrar = false
            }
        }

        if(isEntrar){
            Glide.with(this).load(resources.getDrawable(R.drawable.swords, null)).into(imgAccionGuar)
            txtBtnAccionGuar.text = resources.getString(R.string.guar_entrada)
        }else{
            Glide.with(this).load(resources.getDrawable(R.drawable.shield, null)).into(imgAccionGuar)
            txtBtnAccionGuar.text = resources.getString(R.string.guar_salida)
        }

        contentBtnGuar.setOnClickListener {
            if(isEntrar){
                DialogHelper.getInstance().showTwoButtonsAlert(act!!,
                    resources.getString(R.string.guardias),
                    resources.getString(R.string.warn_guardias),
                    R.drawable.ic_toools_rellena,
                    resources.getString(R.string.yes),
                    completion1 = {
                        DialogHelper.getInstance().showEditTextAlert(
                            act as MainActivity,
                            resources.getString(R.string.descripcion),
                            resources.getString(R.string.desc_aler_desc),
                            R.drawable.ic_toools_rellena,
                            resources.getString(R.string.ok),
                            object : EditTextDialogListener {
                                override fun editTextDialogDismissed(value: String) {
                                    usuario?.token?.let {
                                        viewModel.callAddGuardias(it , Calendar.getInstance().time, value, GuardiasResponse.Guardias.entradaInt )
                                    } ?: run{
                                        viewModel.callAddGuardias("", Calendar.getInstance().time, value, GuardiasResponse.Guardias.entradaInt)
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
                    viewModel.callAddGuardias(it ,Calendar.getInstance().time, "", GuardiasResponse.Guardias.salidaInt)
                } ?: run{
                    viewModel.callAddGuardias("",Calendar.getInstance().time, "", GuardiasResponse.Guardias.salidaInt)
                }
            }
        }
    }

    private val guardiasObservable = Observer<Resource<GuardiasResponse>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(TAG, "guardias: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                if(act!=null) {
                    DialogHelper.getInstance().showLoadingAlert(act!!, null, true)
                }
            }
            Resource.Status.SUCCESS -> {
                if(act!=null) {

                    DialogHelper.getInstance().showLoadingAlert(act!!, null, false)

                    if (resource.data?.error != null && Integer.parseInt(resource.data.error) == ErrorHelper.SESSION_EXPIRED) {
                        DialogHelper.getInstance().showOKAlert(activity = act!!,
                            title = R.string.not_session,
                            text = R.string.desc_not_session,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {
                                if(act != null)
                                    (act as MainActivity).onBackToLogin()
                            })

                    }else {
                        RestRepository.getInstance().usuario!!.zonaHoraria = resource.data!!.timeZone!!
                        cargarGuardias(resource.data.actuaciones, resource.data.timeZone!!)
                    }
                }
            }
            Resource.Status.ERROR -> {
                if(act!=null) {
                    DialogHelper.getInstance().showLoadingAlert(act!!, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = act!!,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.momentosError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            usuario?.token?.let {
                                viewModel.callGuardias(it , dia, mes, anyo)
                            } ?: run{
                                viewModel.callGuardias("" , dia, mes, anyo)
                            }
                        })
                }
            }
        }
    }


    private val addGuardiasObservable = Observer<Resource<GuardiasResponse>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(TAG, "addGuardias: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                if(act!=null) {
                    DialogHelper.getInstance().showLoadingAlert(act!!, null, true)
                }
            }
            Resource.Status.SUCCESS -> {
                if(act!=null) {

                    DialogHelper.getInstance().showLoadingAlert(act!!, null, false)

                    if (resource.data?.error != null && Integer.parseInt(resource.data.error) == ErrorHelper.SESSION_EXPIRED) {
                        DialogHelper.getInstance().showOKAlert(activity = act!!,
                            title = R.string.not_session,
                            text = R.string.desc_not_session,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {
                                if(act != null)
                                    (act as MainActivity).onBackToLogin()
                            })

                    }else {
                        RestRepository.getInstance().usuario!!.zonaHoraria = resource.data!!.timeZone!!
                        cargarGuardias(resource.data.actuaciones, resource.data.timeZone!!)

                        DialogHelper.getInstance().showOKAlert(activity = act!!,
                            title = R.string.guardias,
                            text = R.string.guardias_correcta,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {
                            })
                    }
                }
            }
            Resource.Status.ERROR -> {
                if(act!=null) {
                    DialogHelper.getInstance().showLoadingAlert(act!!, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = act!!,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.momentosError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {

                        })
                }
            }
        }
    }
}*/