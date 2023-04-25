package com.toools.ierp.ui.fichajesFragment

import android.annotation.SuppressLint
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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.toools.ierp.BuildConfig
import com.toools.ierp.IerpApp
import com.toools.ierp.R
import com.toools.ierp.core.*
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.Repository
import com.toools.ierp.data.model.LoginResponse
import com.toools.ierp.databinding.ContentDesdeCasaBinding
import com.toools.ierp.databinding.FragmentFichajeBinding
import com.toools.ierp.ui.base.BaseFragment
import com.toools.ierp.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.*

const val TAG = "FichajeFragment"

@AndroidEntryPoint
class FichajeFragment : BaseFragment() {

    var usuarioPrefs: LoginResponse? = null
    var usuario: LoginResponse? = null
    private lateinit var binding: FragmentFichajeBinding

    private val viewModel: FichajeViewModel by viewModels()

    var longitud: Double? = null
    var latitud: Double? = null

    var dia: Int = 1
    var mes: Int = 7
    var anyo: Int = 2019

    var isEntrar = true

    var formatFecha = SimpleDateFormat("dd' de 'MMMM' de 'yyyy", Locale.getDefault())

    private var adapter: AdapterAcciones? = null

    private var listMomentos: MutableList<LoginResponse.Momentos> = mutableListOf()

    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationUpdates: LocationCallback? = null

    private var timeInterval: Long = 800
    private var minimalDistance: Float = 1.0f

    private fun reqSetting(): LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval).apply {
            setMinUpdateDistanceMeters(minimalDistance)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFichajeBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpObservers()
    }

    override fun onResume() {
        super.onResume()
        onLoadView()
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    fun setUpView(){

        binding.apply{

            //comprobar si existen acciones y cargar el recicler.
            val layoutManager = LinearLayoutManager(activity)
            layoutManager.orientation = RecyclerView.VERTICAL
            recyclerAcc.layoutManager = layoutManager
            recyclerAcc.setHasFixedSize(true)
            val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            recyclerAcc.addItemDecoration(decoration)
            (recyclerAcc.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            showCenter()
            usuario = Repository.usuario

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            //BUSCAR LA UBICACION DEL USUARIO
            locationUpdates = object : LocationCallback() {
                override fun onLocationResult(lr: LocationResult) {
                    //longitud = lr.lastLocation?.longitude
                    //latitud = lr.lastLocation?.latitude
                    longitud = 41.475005
                    latitud = -4.710115
                }
            }

            fusedLocationClient?.requestLocationUpdates(reqSetting(), locationUpdates!!, null)

            contentCalendar.setOnClickListener {
                DatePickerDialog(
                    requireContext(), R.style.DatePickerDialogTheme,
                    { _, year, month, dayOfMonth ->

                        anyo = year
                        mes = month + 1
                        dia = dayOfMonth

                        //creo que se queda parado aqui
                        DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)

                        usuario?.token?.let {
                            viewModel.momentosDia(it, dia, mes, anyo)
                        } ?: run {
                            viewModel.momentosDia("", dia, mes, anyo)
                        }

                    }, anyo, mes - 1, dia
                ).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onLoadView() {

        usuarioPrefs = usuarioPrefs ?: Gson().fromJson(
            IerpApp.getInstance().prefs.getString(ConstantHelper.usuarioLogin, null),
            LoginResponse::class.java
        )
        usuario = Repository.usuario

        val cal = Calendar.getInstance()
        anyo = cal.get(Calendar.YEAR)
        mes = cal.get(Calendar.MONTH) + 1
        dia = cal.get(Calendar.DAY_OF_MONTH)

        usuario?.let {

            binding.apply{
                txtNombreUser.text = usuario!!.nombre
                txtDescUser.text = "@${usuario!!.username}"
                Glide.with(this@FichajeFragment).load(resources.getString(R.string.url_base_img, usuarioPrefs!!.username))
                    .circleCrop()
                    .error(Glide.with(this@FichajeFragment).load(R.drawable.luciano).circleCrop()).into(imgUser)
            }

            cargarAcciones(usuario!!.momentos, usuario!!.zonaHoraria!!)

        } ?:run {
            if(activity != null)
                (activity as MainActivity).onBackToLogin()
        }

    }

    @SuppressLint("MissingPermission")
    private fun cargarAcciones(momentos: MutableList<LoginResponse.Momentos>, zonaHoraria: String) {

        listMomentos.clear()
        listMomentos.addAll(momentos)

        binding.apply{
            val calendar = Calendar.getInstance()
            calendar.set(anyo, mes - 1, dia)
            txtFecha.text = formatFecha.format(calendar.time)

            if (DateUtils.isToday(calendar.time.time)) {
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

                isEntrar = momentos.last().tipo != LoginResponse.Momentos.entrada
            }

            adapter = AdapterAcciones(requireContext(), listMomentos)
            recyclerAcc.adapter = adapter

            contentBtn.setOnClickListener {

                if (latitud != null && longitud != null) {

                    if (usuarioPrefs?.permitir_no_localizacion == "0") {
                        fichar()
                    } else {
                        checkDistance()
                    }
                } else {
                    Toasty.warning(requireActivity(), resources.getString(R.string.warn_sin_localizacion)).show()
                }
            }
        }
    }

    fun checkDistance() {

        var distanciaKO = true
        var minDistance = 1000000f
        var centerName = ""

        val locUser = Location("user")
        locUser.latitude = latitud!!
        locUser.longitude = longitud!!

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
                            activity = requireActivity(),
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
        IerpApp.getInstance().prefs.getString(
            ConstantHelper.centroTrabajo,
            null
        )?.let { centerDescr ->
            binding.txtCentro.text = resources.getString(
                R.string.trabajando_centro,
                centerDescr
            )
        }
    }

    fun saveCenter(centro: String) {
        var editor = IerpApp.getInstance().prefs.edit()
        editor.putString(ConstantHelper.centroTrabajo, centro)
        editor.apply()
    }

    fun resetCenter() {
        var editor = IerpApp.getInstance().prefs.edit()
        editor.putString(ConstantHelper.centroTrabajo, null)
        editor.apply()
    }

    fun fichar() {
        val entrar =
            if (isEntrar) {
                LoginResponse.Momentos.entradaInt
            } else {
                LoginResponse.Momentos.salidaInt
            }

        usuario?.token?.let {
            viewModel.entradaSalida(
                it,
                entrar,
                latitud!!,
                longitud!!,
                "", ""
            )
        } ?: run {
            viewModel.entradaSalida(
                "",
                entrar,
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

        binding.apply{
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

                    Glide.with(this@FichajeFragment).load(resources.getDrawable(R.drawable.finish, null))
                        .into(imgAccion)

                    txtBtnAccion.text = resources.getString(R.string.finish_accion)

                } else {
                    if (handler != null)
                        runnable?.let { handler!!.removeCallbacks(it) }

                    txtTotalHoras.text =
                        resources.getString(R.string.time_trabajo, milisecondToString())

                    Glide.with(this@FichajeFragment).load(resources.getDrawable(R.drawable.start, null))
                        .into(imgAccion)
                    txtBtnAccion.text = resources.getString(R.string.start_accion)
                    resetCenter()
                    txtCentro.text = ""
                }

            } else {
                Glide.with(this@FichajeFragment).load(resources.getDrawable(R.drawable.start, null)).into(imgAccion)
                txtBtnAccion.text = resources.getString(R.string.start_accion)
                resetCenter()
                txtCentro.text = ""
            }
        }

        return segundosTrabajo
    }

    var runnable: Runnable? = null
    var handler: Handler? = null
    private fun timer() {
        binding.apply {
            if (handler != null)
                runnable?.let { handler!!.removeCallbacks(it) }
            else
                handler = Handler(Looper.getMainLooper())

            runnable = Runnable {
                segundosTrabajo += 1000
                txtTotalHoras.text =
                    resources.getString(R.string.time_trabajo, milisecondToString())
                timer()
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

    private fun cargarModalCasa(isEntrar: Boolean, distancia: Long, centro: String) {

        val codigo = "codigo"
        binding.contentDesdeCasa.apply{

            // muestra la vista content desde casa
            containerCasa.visibility = View.VISIBLE
            activity?.let {
                (it as MainActivity).mostrarToolbar(false)
            }

            if (isEntrar) {
                txtDescCasa.text =
                    resources.getString(R.string.txt_entrar_casa, distancia, centro)
                Glide.with(this@FichajeFragment).load(resources.getDrawable(R.drawable.start, null))
                    .into(imgAccionCasa)
                txtBtnAccionCasa.text = resources.getString(R.string.start_accion)
            } else {
                txtDescCasa.text =
                    resources.getString(R.string.txt_salir_casa, distancia, centro)
                Glide.with(this@FichajeFragment).load(resources.getDrawable(R.drawable.finish, null))
                    .into(imgAccionCasa)
                txtBtnAccionCasa.text = resources.getString(R.string.finish_accion)
            }
            contentBtncasa.setOnClickListener {
                if (isEntrar) {
                    DialogHelper.getInstance().showEditTextAlert(
                        requireActivity(),
                        resources.getString(R.string.insert_code_title),
                        resources.getString(R.string.insert_code_description),
                        R.drawable.ic_toools_rellena,
                        resources.getString(R.string.ok),
                        object : EditTextDialogListener {
                            override fun editTextDialogDismissed(value: String) {

                                DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)

                                usuario?.token?.let {
                                    viewModel.entradaSalida(
                                        it,
                                        LoginResponse.Momentos.entradaInt,
                                        latitud!!,
                                        longitud!!,
                                        codigo, value
                                    )
                                } ?: run {
                                    viewModel.entradaSalida(
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
                    DialogHelper.getInstance().showLoadingAlert(requireActivity() ,null,true)

                    usuario?.token?.let {
                        viewModel.entradaSalida(
                            it,
                            LoginResponse.Momentos.salidaInt,
                            latitud!!,
                            longitud!!,
                            codigo, ""
                        )
                    } ?: run {
                        viewModel.entradaSalida(
                            "",
                            LoginResponse.Momentos.salidaInt,
                            latitud!!,
                            longitud!!,
                            codigo, ""
                        )
                    }
                }
                containerCasa.visibility = View.GONE
                activity?.let {
                    (it as MainActivity).mostrarToolbar(true)
                }
            }

            cardVolver.setOnClickListener {
                // oculta la vista content desde casa
                containerCasa.visibility = View.GONE
                activity?.let {
                    (it as MainActivity).mostrarToolbar(true)
                }
            }
        }

    }

    fun setUpObservers(){

        //momentosDia
        viewModel.momentosDiaLiveData.observe(viewLifecycleOwner){ response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "momentos: {${response.status}}")

            when (response.status) {
                Resource.Status.LOADING -> {
                     DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                }
                Resource.Status.SUCCESS -> {

                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    if (response.data?.error != null && Integer.parseInt(response.data.error) == ErrorHelper.SESSION_EXPIRED) {
                        DialogHelper.getInstance().showOKAlert(requireActivity(),
                            title = R.string.not_session,
                            text = R.string.desc_not_session,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {
                                if(activity != null)
                                    (activity as MainActivity).onBackToLogin()
                            })

                    } else {
                        usuario!!.zonaHoraria = response.data!!.timeZone!!
                        cargarAcciones(response.data.momentos, response.data.timeZone!!)
                    }
                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    DialogHelper.getInstance().showOKAlert(requireActivity(),
                        title = R.string.ups,
                        text = response.exception ?: ErrorHelper.momentosError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {
                            usuario?.token?.let {
                                if (BuildConfig.DEBUG)
                                    Log.e(TAG, "vamos a momentos DESDE MOMENTOS PORQUE ALGO FUE MAL")
                                viewModel.momentosDia(it, dia, mes, anyo)
                            } ?: run {
                                if (BuildConfig.DEBUG)
                                    Log.e(TAG, "vamos a momentos DESDE MOMENTOS PORQUE ALGO FUE MAL")
                                viewModel.momentosDia("", dia, mes, anyo)
                            }
                        })
                }
            }
        }

        //entradaSalida
        viewModel.entradaSalidaLiveData.observe(viewLifecycleOwner){ response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "entradaSalida: {${response.status}}")

            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)

                    if (response.data?.error != null && Integer.parseInt(response.data.error) == ErrorHelper.SESSION_EXPIRED) {
                        DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                            title = R.string.not_session,
                            text = R.string.desc_not_session,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {
                                if(activity != null)
                                    (activity as MainActivity).onBackToLogin()
                            })

                    } else {
                        try {
                            usuario!!.momentos = response.data!!.momentos
                            cargarAcciones(response.data.momentos, usuarioPrefs!!.zonaHoraria!!)
                        } catch (e: Exception) {

                        }
                    }
                    DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                    Handler(Looper.getMainLooper()).postDelayed({
                            DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                    }, 500)
                }
                Resource.Status.ERROR -> {
                        DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                        DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                            title = R.string.ups,
                            text = response.exception ?: ErrorHelper.addEventError,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {}
                        )
                }
            }
        }
    }
}