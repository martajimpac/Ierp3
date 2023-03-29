package com.toools.ierp.ui.fichajesFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
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
import com.google.android.gms.location.*
import com.toools.ierp.ui.base.BaseFragment
import com.toools.ierp.ui.main.MainActivity
import es.dmoral.toasty.Toasty

import java.text.SimpleDateFormat
import java.util.*


const val TAG = "FichajeFragment"

class FichajeFragment : BaseFragment() {

    /*

    private var act: Activity? = null
    var usuario: LoginResponse? = null
    var usuarioPrefs: LoginResponse? = null

    val reqSetting: LocationRequest = LocationRequest.create().apply {
        fastestInterval = 1000
        interval = 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 1.0f
    }


    private var mLastClickTime: Long = 0
    val builder = LocationSettingsRequest.Builder().addLocationRequest(reqSetting)
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationUpdates: LocationCallback? = null

    var longitud: Double? = null
    var latitud: Double? = null

    var dia: Int = 1
    var mes: Int = 7
    var anyo: Int = 2019

    var isEntrar = true

    var formatFecha = SimpleDateFormat("dd' de 'MMMM' de 'yyyy", Locale.getDefault())

    private var adapter: AdapterAcciones? = null

    private var listMomentos: MutableList<LoginResponse.Momentos> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fichaje, container, false)
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act?.let { act ->

            //comprobar si existen acciones y cargar el recicler.
            val layoutManager = LinearLayoutManager(act)
            layoutManager.orientation = RecyclerView.VERTICAL
            recyclerAcc.layoutManager = layoutManager
            recyclerAcc.setHasFixedSize(true)
            val decoration = DividerItemDecoration(act, DividerItemDecoration.VERTICAL)
            recyclerAcc.addItemDecoration(decoration)
            (recyclerAcc.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            showCenter()

            onLoadView()

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(act)

            locationUpdates = object : LocationCallback() {
                override fun onLocationResult(lr: LocationResult) {
                    longitud = lr.lastLocation?.longitude
                    latitud = lr.lastLocation?.latitude
                }
            }

            fusedLocationClient?.requestLocationUpdates(reqSetting, locationUpdates!!, null)

            contentCalendar.setOnClickListener {

                DatePickerDialog(
                    act as Context, R.style.DatePickerDialogTheme,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

                        anyo = year
                        mes = month + 1
                        dia = dayOfMonth

                        DialogHelper.getInstance().showLoadingAlert(act, null, true)

                        usuario?.token?.let {
                            viewModel.callMomentosDia(it, dia, mes, anyo)
                        } ?: run {
                            viewModel.callMomentosDia("", dia, mes, anyo)
                        }

                    }, anyo, mes - 1, dia
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        onLoadView()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            act = context
    }

    private val viewModel: FichajeViewModel by lazy {

        ViewModelProvider(
            this,
            this.defaultViewModelProviderFactory
        ).get(FichajeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.momentosDiaRecived.observe(this, momentosDiaObserver)
        viewModel.addEventRecived.observe(this, addEventObserver)
    }

    @SuppressLint("SetTextI18n")
    private fun onLoadView() {

        val cal = Calendar.getInstance()
        anyo = cal.get(Calendar.YEAR)
        mes = cal.get(Calendar.MONTH) + 1
        dia = cal.get(Calendar.DAY_OF_MONTH)

        usuario = RestRepository.getInstance().usuario

        if (usuario != null) {

            txtNombreUser.text = usuario!!.nombre
            txtDescUser.text = "@${usuario!!.username}"

            Glide.with(this).load(resources.getString(R.string.url_base_img, usuario!!.username))
                .circleCrop()
                .error(Glide.with(this).load(R.drawable.luciano).circleCrop()).into(imgUser)

            cargarAcciones(usuario!!.momentos, usuario!!.zonaHoraria!!)

        } else {

            if (act != null)
                (act as MainActivity).onBackToLogin()
        }

    }

    @SuppressLint("MissingPermission")
    private fun cargarAcciones(momentos: MutableList<LoginResponse.Momentos>, zonaHoraria: String) {

        listMomentos.clear()
        listMomentos.addAll(momentos)

        val cal = Calendar.getInstance()
        cal.set(anyo, mes - 1, dia)
        txtFecha.text = formatFecha.format(cal.time)

        if (DateUtils.isToday(cal.time.time)) {
            contentBtn.isEnabled = true
            contentBtn.setCardBackgroundColor(resources.getColor(R.color.colorPrimary, null))
        } else {
            contentBtn.isEnabled = false
            contentBtn.setCardBackgroundColor(resources.getColor(R.color.colorAccent, null))
        }

        if (momentos.isNullOrEmpty() || momentos.size == 0) {

            txtTituRegistros.text = resources.getString(R.string.not_entradas)

            txtTituAcciones.text = resources.getString(R.string.desc_not_entradas)

            txtTotalHoras.visibility = View.INVISIBLE

        } else {

            txtTituRegistros.text = resources.getString(R.string.entra_sal)

            txtTituAcciones.text = resources.getString(R.string.txt_horas, zonaHoraria)

            txtTotalHoras.visibility = View.VISIBLE

            //contador de horas
            obtenerTiempo(momentos)

            if (momentos.last().tipo == LoginResponse.Momentos.entrada) {
                isEntrar = false
            } else {
                isEntrar = true
            }
        }

        //cargar el recycler
        /*if (adapter != null) {
            adapter!!.setList(listMomentos)
        } else {
            adapter = AdapterAcciones(act as Context, listMomentos)
            recyclerAcc.adapter = adapter
        }*/

        adapter = AdapterAcciones(act as Context, listMomentos)
        recyclerAcc.adapter = adapter

        contentBtn.setOnClickListener {
            if (latitud != null && longitud != null) {

                usuarioPrefs = Gson().fromJson(
                    Application.getInstance().prefs.getString(
                        ConstantsHelper.usuarioLogin,
                        null
                    ), LoginResponse::class.java
                )

                if (usuarioPrefs?.permitir_no_localizacion == "0") {
                    fichar()
                } else {
                    checkDistance()
                }
            } else
                Toasty.warning(act!!, resources.getString(R.string.warn_sin_localizacion)).show()
        }
    }

    fun checkDistance() {
        usuarioPrefs = Gson().fromJson(
            Application.getInstance().prefs.getString(
                ConstantsHelper.usuarioLogin,
                null
            ), LoginResponse::class.java
        )

        var distanciaKO = true
        var minDistance = 1000000f
        var centerName = ""

        val locUser = Location("user")
        locUser.latitude = latitud!!
        locUser.longitude = longitud!!

//        usuarioPrefs?.id_centro_trabajo = "1"

        if (usuarioPrefs?.id_centro_trabajo == "0") {
            for (centro in usuarioPrefs!!.centros_trabajo) {
                val locWork = Location("work")
                locWork.latitude = centro.ct_latitud!!.toDouble()
                locWork.longitude = centro.ct_longitud!!.toDouble()

                if (locUser.distanceTo(locWork) < centro.ct_distancia_fichajes!!.toDouble()) {
                    distanciaKO = false
                    fichar()
                    break
                }

                if (locUser.distanceTo(locWork) < minDistance) {
                    minDistance = locUser.distanceTo(locWork)
                    centerName = centro.ct_nombre ?: ""
                }
            }
        } else {
            for (centro in usuarioPrefs!!.centros_trabajo) {
                val locWork = Location("work")
                locWork.latitude = centro.ct_latitud!!.toDouble()
                locWork.longitude = centro.ct_longitud!!.toDouble()

                if (locUser.distanceTo(locWork) < centro.ct_distancia_fichajes!!.toDouble()) {
                    distanciaKO = false
                    if (usuarioPrefs?.id_centro_trabajo == centro.ct_id) {
                        fichar()
                        break
                    } else {
                        DialogHelper.getInstance().showTwoButtonsAlert(
                            activity = act!!,
                            title = R.string.distinto_centro,
                            text = R.string.centro_no_asignado,
                            icon = R.drawable.ic_toools_rellena,
                            button1 = R.string.yes,
                            completion1 = {
                                centro.ct_descripcion?.let{saveCenter(it)}

                                showCenter()

                                distanciaKO = false
                                fichar()
                            },
                            button2 = R.string.cancel,
                            completion2 = {
                            }
                        )
                        break
                    }
                }

                if (usuarioPrefs?.id_centro_trabajo == centro.ct_id) {
                    minDistance = locUser.distanceTo(locWork)
                    centerName = centro.ct_nombre ?: ""
                }
            }
        }

        if (distanciaKO) {
            cargarModalCasa(isEntrar, minDistance.toLong(), centerName)
        }
    }

    fun showCenter(){
        Application.getInstance().prefs.getString(
            ConstantsHelper.centroTrabajo,
            null
        )?.let { centerDescr ->

            txtCentro.text = resources.getString(
                R.string.trabajando_centro,
                centerDescr
            )
        }
    }

    fun saveCenter(centro: String) {
        var editor = Application.getInstance().prefs.edit()
        editor.putString(ConstantsHelper.centroTrabajo, centro)
        editor.apply()
    }

    fun resetCenter() {
        var editor = Application.getInstance().prefs.edit()
        editor.putString(ConstantsHelper.centroTrabajo, null)
        editor.apply()
    }

    fun fichar() {

        /*val locUser = Location("user")
        locUser.latitude = latitud!!
        locUser.longitude = longitud!!*/

        val entradaSalida =

            if (isEntrar) {
                LoginResponse.Momentos.entradaInt
            } else {
                LoginResponse.Momentos.salidaInt
            }

        usuario?.token?.let {
            viewModel.callAddEvent(
                it,
                entradaSalida,
                latitud!!,
                longitud!!,
                "", ""
            )
        } ?: run {
            viewModel.callAddEvent(
                "",
                entradaSalida,
                latitud!!,
                longitud!!,
                "", ""
            )
        }
    }

    var segundosTrabajo: Long = 0
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formatDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun obtenerTiempo(momentos: MutableList<LoginResponse.Momentos>): Long {

        segundosTrabajo = 0

        if (momentos.size > 0) {
            var monAux = momentos[0]
            var cont = 1
            while (momentos.size >= cont + 1) {

                val mon = momentos[cont]

                if (mon.tipo == LoginResponse.Momentos.salida) {
                    segundosTrabajo += format.parse(mon.momento).time - format.parse(monAux.momento).time
                }

                monAux = mon
                cont += 1
            }

            if (monAux.tipo == LoginResponse.Momentos.entrada) {

                if (DateUtils.isToday(format.parse(momentos.last().momento).time)) {
                    segundosTrabajo += Date().time - format.parse(monAux.momento).time
                    timer()
                } else {

                    segundosTrabajo += format.parse(formatDay.format(format.parse(monAux.momento)) + " 23:59:59").time - format.parse(
                        monAux.momento
                    ).time

                    txtTotalHoras.text =
                        resources.getString(R.string.time_trabajo, milisecondToString())
                }

                Glide.with(this).load(resources.getDrawable(R.drawable.finish, null))
                    .into(imgAccion)

                txtBtnAccion.text = resources.getString(R.string.finish_accion)

            } else {
                if (handler != null)
                    runnable?.let { handler!!.removeCallbacks(it) }

                txtTotalHoras.text =
                    resources.getString(R.string.time_trabajo, milisecondToString())

                Glide.with(this).load(resources.getDrawable(R.drawable.start, null))
                    .into(imgAccion)
                txtBtnAccion.text = resources.getString(R.string.start_accion)
                resetCenter()
                txtCentro.text = ""
            }

        } else {
            Glide.with(this).load(resources.getDrawable(R.drawable.start, null)).into(imgAccion)
            txtBtnAccion.text = resources.getString(R.string.start_accion)
            resetCenter()
            txtCentro.text = ""
        }

        return segundosTrabajo

    }

    var runnable: Runnable? = null
    var handler: Handler? = null
    private fun timer() {

        if (txtTotalHoras != null) {

            if (handler != null)
                runnable?.let { handler!!.removeCallbacks(it) }
            else
                handler = Handler(Looper.getMainLooper())

            runnable = Runnable {

                if (txtTotalHoras != null) {

                    segundosTrabajo += 1000
                    txtTotalHoras.text =
                        resources.getString(R.string.time_trabajo, milisecondToString())
                    timer()
                }
            }

            handler!!.postDelayed(runnable!!, 1000)
        }
    }

    private fun milisecondToString(): String {

        val hora = segundosTrabajo / 3600000
        val restohora = segundosTrabajo % 3600000
        val minuto = restohora / 60000
        val segundo = restohora % 60000 / 1000

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hora, minuto, segundo)
    }

    private var modalCasa: View? = null
    private fun cargarModalCasa(isEntrar: Boolean, distancia: Long, centro: String) {

        val inflater = LayoutInflater.from(act)

        modalCasa =
            inflater.inflate(R.layout.content_desde_casa, (act as MainActivity).content, false)
        (act as MainActivity).content.addView(modalCasa)

        if (isEntrar) {
            modalCasa!!.txtDescCasa.text =
                resources.getString(R.string.txt_entrar_casa, distancia, centro)
            Glide.with(this).load(resources.getDrawable(R.drawable.start, null))
                .into(modalCasa!!.imgAccionCasa)
            modalCasa!!.txtBtnAccionCasa.text = resources.getString(R.string.start_accion)
        } else {
            modalCasa!!.txtDescCasa.text =
                resources.getString(R.string.txt_salir_casa, distancia, centro)
            Glide.with(this).load(resources.getDrawable(R.drawable.finish, null))
                .into(modalCasa!!.imgAccionCasa)
            modalCasa!!.txtBtnAccionCasa.text = resources.getString(R.string.finish_accion)
        }

        modalCasa!!.contentBtncasa.setOnClickListener {

            val codigo = "codigo"

            if (isEntrar) {

                DialogHelper.getInstance().showEditTextAlert(
                    act as MainActivity,
//                    resources.getString(R.string.descripcion),
                    resources.getString(R.string.insert_code_title),
                    resources.getString(R.string.insert_code_description),
                    R.drawable.ic_toools_rellena,
                    resources.getString(R.string.ok),
                    object : EditTextDialogListener {
                        override fun editTextDialogDismissed(value: String) {

                            DialogHelper.getInstance().showLoadingAlert(act!!, null, true)

                            usuario?.token?.let {
                                viewModel.callAddEvent(
                                    it,
                                    LoginResponse.Momentos.entradaInt,
                                    latitud!!,
                                    longitud!!,
                                    codigo, value
                                )
                            } ?: run {
                                viewModel.callAddEvent(
                                    "",
                                    LoginResponse.Momentos.entradaInt,
                                    latitud!!,
                                    longitud!!,
                                    codigo, value
                                )
                            }

                        }
                    },
                    button2 = resources.getString(R.string.cancel),
                    hint = resources.getString(R.string.descripcion)
                )
            } else {
                DialogHelper.getInstance().showLoadingAlert(act!!, null, true)

                usuario?.token?.let {
                    viewModel.callAddEvent(
                        it,
                        LoginResponse.Momentos.salidaInt,
                        latitud!!,
                        longitud!!,
                        codigo, ""
                    )
                } ?: run {
                    viewModel.callAddEvent(
                        "",
                        LoginResponse.Momentos.salidaInt,
                        latitud!!,
                        longitud!!,
                        codigo, ""
                    )
                }
            }
        }

        modalCasa!!.cardVolver.setOnClickListener {
            if (modalCasa != null)
                (act as MainActivity).content.removeView(modalCasa)
        }
    }

    private val momentosDiaObserver = Observer<Resource<MomentosResponse>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(com.toools.ierp.ui.login.TAG, "momentos: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                if (act != null) {
                    DialogHelper.getInstance().showLoadingAlert(act!!, null, true)
                }
            }
            Resource.Status.SUCCESS -> {
                if (act != null) {

                    DialogHelper.getInstance().showLoadingAlert(act!!, null, false)

                    if (resource.data?.error != null && Integer.parseInt(resource.data.error) == ErrorHelper.SESSION_EXPIRED) {
                        DialogHelper.getInstance().showOKAlert(activity = act!!,
                            title = R.string.not_session,
                            text = R.string.desc_not_session,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {
                                if (act != null)
                                    (act as MainActivity).onBackToLogin()
                            })

                    } else {
                        RestRepository.getInstance().usuario!!.zonaHoraria =
                            resource.data!!.timeZone!!
                        cargarAcciones(resource.data.momentos, resource.data.timeZone!!)
                    }
                }
            }
            Resource.Status.ERROR -> {
                if (act != null) {
                    DialogHelper.getInstance().showLoadingAlert(act!!, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = act!!,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.momentosError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            usuario?.token?.let {
                                viewModel.callMomentosDia(it, dia, mes, anyo)
                            } ?: run {
                                viewModel.callMomentosDia("", dia, mes, anyo)
                            }
                        })
                }
            }
        }
    }

    private val addEventObserver = Observer<Resource<MomentosResponse>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(com.toools.ierp.ui.login.TAG, "addEvent: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                if (act != null) {
                    DialogHelper.getInstance().showLoadingAlert(act!!, null, true)
                }
            }
            Resource.Status.SUCCESS -> {
                if (act != null) {
                    DialogHelper.getInstance().showLoadingAlert(act!!, null, false)

                    if (modalCasa != null)
                        (act as MainActivity).content.removeView(modalCasa)

                    if (resource.data?.error != null && Integer.parseInt(resource.data.error) == ErrorHelper.SESSION_EXPIRED) {
                        DialogHelper.getInstance().showOKAlert(activity = act!!,
                            title = R.string.not_session,
                            text = R.string.desc_not_session,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {
                                if (act != null)
                                    (act as MainActivity).onBackToLogin()
                            })

                    } else {
                        try {
                            if (resource.data!!.momentos.last().tipo == LoginResponse.Momentos.entrada) {
//                                Toasty.warning(act!!, resources.getString(R.string.ini_trabajo))
//                                    .show()

                            } else {
//                                Toasty.warning(act!!, resources.getString(R.string.fin_trabajo))
//                                    .show()
                            }

                            RestRepository.getInstance().usuario!!.momentos = resource.data.momentos
                            cargarAcciones(resource.data.momentos, usuario!!.zonaHoraria!!)
                        } catch (e: Exception) {

                        }
                    }
                    act?.let { it1 -> DialogHelper.getInstance().showLoadingAlert(it1, null, true) }
                    Handler(Looper.getMainLooper()).postDelayed({
                        act?.let { it1 ->
                            DialogHelper.getInstance().showLoadingAlert(it1, null, false)
                        }
                    }, 1000)
                }
            }
            Resource.Status.ERROR -> {
                if (act != null) {
                    DialogHelper.getInstance().showLoadingAlert(act!!, null, false)
                    DialogHelper.getInstance().showOKAlert(activity = act!!,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.addEventError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {})
                }
            }
        }
    }

     */
}