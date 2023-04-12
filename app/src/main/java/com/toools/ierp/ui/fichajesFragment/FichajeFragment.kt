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
    /*

    private var activity: Activity? = null
    var usuario: LoginResponse? = null
    private lateinit var binding: FragmentFichajeBinding
    private lateinit var casaBinding: ContentDesdeCasaBinding

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

    /* todo ver si va bien
        val reqSetting: LocationRequest = LocationRequest.create().apply {
        fastestInterval = 1000
        interval = 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 1.0f
    }*/
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
        casaBinding = ContentDesdeCasaBinding.inflate(inflater, container,false)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            activity = context
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    fun setUpView(){
        activity?.let { activity ->
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

                onLoadView() //todo porque aqui llama a on load view? si lo quito sigue funcionando?

                fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

                locationUpdates = object : LocationCallback() {
                    override fun onLocationResult(lr: LocationResult) {
                        longitud = lr.lastLocation?.longitude
                        latitud = lr.lastLocation?.latitude
                    }
                }

                fusedLocationClient?.requestLocationUpdates(reqSetting(), locationUpdates!!, null)

                contentCalendar.setOnClickListener {

                    DatePickerDialog(
                        activity as Context, R.style.DatePickerDialogTheme,
                        { _, year, month, dayOfMonth ->

                            anyo = year
                            mes = month + 1
                            dia = dayOfMonth

                            DialogHelper.getInstance().showLoadingAlert(activity, null, true)

                            //todo porque hace esto?? Y SI MOMENTOS DIA NO FUERA NULL?
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

    }

    @SuppressLint("SetTextI18n")  //todo que diferencia hay entre set up view y load view??
    private fun onLoadView() {

        val cal = Calendar.getInstance()
        anyo = cal.get(Calendar.YEAR)
        mes = cal.get(Calendar.MONTH) + 1
        dia = cal.get(Calendar.DAY_OF_MONTH)

        usuario = Gson().fromJson(
            IerpApp.getInstance().prefs.getString(ConstantHelper.usuarioLogin, null),
            LoginResponse::class.java
        )

        usuario?.let {

            binding.apply{
                txtNombreUser.text = usuario!!.nombre
                txtDescUser.text = "@${usuario!!.username}"

                Glide.with(this@FichajeFragment).load(resources.getString(R.string.url_base_img, usuario!!.username))
                    .circleCrop()
                    .error(Glide.with(this@FichajeFragment).load(R.drawable.luciano).circleCrop()).into(imgUser)
            }

            cargarAcciones(usuario!!.momentos, usuario!!.zonaHoraria!!)

        } ?:run {
            if (activity != null)
                (activity as MainActivity).onBackToLogin()
        }

    }

    @SuppressLint("MissingPermission")
    private fun cargarAcciones(momentos: MutableList<LoginResponse.Momentos>, zonaHoraria: String) {

        Log.e(com.toools.ierp.ui.login.TAG, "cargar ACCIONES")
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

                Log.e(com.toools.ierp.ui.login.TAG, "cargar MOMENTOS ES NULLL")
                txtTituRegistros.text = resources.getString(R.string.not_entradas)

                txtTituAcciones.text = resources.getString(R.string.desc_not_entradas)

                txtTotalHoras.visibility = View.INVISIBLE

            } else {

                Log.e(com.toools.ierp.ui.login.TAG, "cargar MOMENTOS NO ES NULLL")
                txtTituRegistros.text = resources.getString(R.string.entra_sal)

                txtTituAcciones.text = resources.getString(R.string.txt_horas, zonaHoraria)

                txtTotalHoras.visibility = View.VISIBLE

                //contador de horas
                obtenerTiempo(momentos)

                isEntrar = momentos.last().tipo != LoginResponse.Momentos.entrada
            }

            adapter = AdapterAcciones(activity as Context, listMomentos)
            recyclerAcc.adapter = adapter

            contentBtn.setOnClickListener {//todo ver porque aqui no carga nada
                if (latitud != null && longitud != null) {

                    usuario = Gson().fromJson(
                        requireContext().prefs.getString(ConstantHelper.usuarioLogin, null),
                        LoginResponse::class.java
                    )

                    if (usuario?.permitir_no_localizacion == "0") {
                        Log.e(com.toools.ierp.ui.login.TAG, "VAMOS A FICHAR PORQUE EL USUARIO NO TIENE LOC")
                        fichar()
                    } else {
                        Log.e(com.toools.ierp.ui.login.TAG, "VAMOS A VER SI ESTAS LEJOS O NO")
                        checkDistance()
                    }
                } else
                    Log.e(com.toools.ierp.ui.login.TAG, "ALGO FUE MAL AL PULSAR EL BOTON, WARN SIN LOC")
                    Toasty.warning(requireActivity(), resources.getString(R.string.warn_sin_localizacion)).show() //TODO: PERMITIR MOSTRAR UBIC DE NUEVO
            }
        }
    }

    fun checkDistance() {
        usuario= Gson().fromJson(
            requireContext().prefs.getString(ConstantHelper.usuarioLogin, null),
            LoginResponse::class.java
        )

        var distanciaKO = true
        var minDistance = 1000000f
        var centerName = ""

        val locUser = Location("user")
        locUser.latitude = latitud!!
        locUser.longitude = longitud!!

        if (usuario?.id_centro_trabajo == "0") {
            Log.e(com.toools.ierp.ui.login.TAG, "check distance: centro trabajo es 0")
            for (centro in usuario!!.centros_trabajo) {
                val locWork = Location("work")
                locWork.latitude = centro.ct_latitud!!.toDouble()
                locWork.longitude = centro.ct_longitud!!.toDouble()

                if (locUser.distanceTo(locWork) < centro.ct_distancia_fichajes!!.toDouble()) {
                    distanciaKO = false
                    fichar()
                    Log.e(com.toools.ierp.ui.login.TAG, "check distance: centro trabajo es 0 fichar")
                    break
                }

                if (locUser.distanceTo(locWork) < minDistance) {
                    minDistance = locUser.distanceTo(locWork)
                    centerName = centro.ct_nombre ?: ""
                    Log.e(com.toools.ierp.ui.login.TAG, "check distance: centro trabajo es 0 no fichar")
                }
            }
        } else {
            for (centro in usuario!!.centros_trabajo) {
                val locWork = Location("work")
                locWork.latitude = centro.ct_latitud!!.toDouble()
                locWork.longitude = centro.ct_longitud!!.toDouble()

                if (locUser.distanceTo(locWork) < centro.ct_distancia_fichajes!!.toDouble()) {
                    distanciaKO = false
                    if (usuario?.id_centro_trabajo == centro.ct_id) {
                        Log.e(com.toools.ierp.ui.login.TAG, "check distance: centro trabajo NO es 0 fichar")
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

                if (usuario?.id_centro_trabajo == centro.ct_id) {
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

        val entradaSalida =

            if (isEntrar) {
                LoginResponse.Momentos.entradaInt
            } else {
                LoginResponse.Momentos.salidaInt
            }

        usuario?.token?.let {
            viewModel.entradaSalida(
                it,
                entradaSalida,
                latitud!!,
                longitud!!,
                "", ""
            )
        } ?: run {
            viewModel.entradaSalida(
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
        Toasty.warning(requireContext(), "cargar modal casa").show()

        /*
        /*modalCasa =
            inflater.inflate(R.layout.content_desde_casa, (act as MainActivity).content, false)
        (act as MainActivity).content.addView(modalCasa) */

        /*
        casaBinding.apply{
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

         */

            casaBinding.apply{
                contentBtncasa.setOnClickListener {
                if (isEntrar) {
                    DialogHelper.getInstance().showEditTextAlert(
                        activity as MainActivity,
                        resources.getString(R.string.insert_code_title),
                        resources.getString(R.string.insert_code_description),
                        R.drawable.ic_toools_rellena,
                        resources.getString(R.string.ok),
                        object : EditTextDialogListener {
                            override fun editTextDialogDismissed(value: String) {

                                DialogHelper.getInstance().showLoadingAlert(activity!!, null, true)

                                usuario?.token?.let {
                                    viewModel.entradaSalida(
                                        it,
                                        LoginResponse.Momentos.entradaInt,
                                        latitud!!,
                                        longitud!!,
                                        "", value
                                    )
                                } ?: run {
                                    viewModel.entradaSalida(
                                        "",
                                        LoginResponse.Momentos.entradaInt,
                                        latitud!!,
                                        longitud!!,
                                        "", value
                                    )
                                }

                            }
                        },
                        button2 = resources.getString(R.string.cancel),
                        hint = resources.getString(R.string.descripcion)
                    )
                } else {
                    DialogHelper.getInstance().showLoadingAlert(requireContext(), null, true)

                    usuario?.token?.let {
                        viewModel.entradaSalida(
                            it,
                            LoginResponse.Momentos.salidaInt,
                            latitud!!,
                            longitud!!,
                            "", ""
                        )
                    } ?: run {
                        viewModel.entradaSalida(
                            "",
                            LoginResponse.Momentos.salidaInt,
                            latitud!!,
                            longitud!!,
                            "", ""
                        )
                    }
                }
            }

            cardVolver.setOnClickListener {
               /* if (modalCasa != null) { //todo como cambio esto?
                    (activity as MainActivity).content.removeView(modalCasa)
                }*/
            }
        }

         */
    }

    fun setUpObservers(){

        //momentosDia
        viewModel.momentosDiaLiveData.observe(viewLifecycleOwner){ response ->
            if (BuildConfig.DEBUG)
                Log.e(com.toools.ierp.ui.login.TAG, "momentos: {${response.status}}")

            when (response.status) {
                Resource.Status.LOADING -> {
                    if (activity != null) {
                        DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                    }
                }
                Resource.Status.SUCCESS -> {
                    if (activity != null) {

                        DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                        if (response.data?.error != null && Integer.parseInt(response.data.error) == ErrorHelper.SESSION_EXPIRED) {
                            DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                                title = R.string.not_session,
                                text = R.string.desc_not_session,
                                icon = R.drawable.ic_toools_rellena,
                                completion = {
                                    if (activity != null)
                                        (activity as MainActivity).onBackToLogin()
                                })

                        } else {
                            usuario!!.zonaHoraria = response.data!!.timeZone!!
                            requireContext().prefs.edit().putString(ConstantHelper.usuarioLogin, Gson().toJson(response.data)).apply()
                            cargarAcciones(response.data.momentos, response.data.timeZone!!)
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    if (activity != null) {
                        DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                        DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                            title = R.string.ups,
                            text = response.exception ?: ErrorHelper.momentosError,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {
                                usuario?.token?.let {
                                    viewModel.momentosDia(it, dia, mes, anyo)
                                } ?: run {
                                    viewModel.momentosDia("", dia, mes, anyo)
                                }
                            })
                    }
                }
            }
        }

        //entradaSalida
        viewModel.entradaSalidaLiveData.observe(viewLifecycleOwner){ response ->
            if (BuildConfig.DEBUG)
                Log.e(com.toools.ierp.ui.login.TAG, "entradaSalida: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    if (activity != null) {
                        DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, true)
                    }
                }
                Resource.Status.SUCCESS -> {
                    if (activity != null) {
                        DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)


                        /* todo porque esta esto aqui
                        modalCasa?.let{
                            (activity as MainActivity).content.removeView(modalCasa)
                        }*/


                        if (response.data?.error != null && Integer.parseInt(response.data.error) == ErrorHelper.SESSION_EXPIRED) {
                            DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                                title = R.string.not_session,
                                text = R.string.desc_not_session,
                                icon = R.drawable.ic_toools_rellena,
                                completion = {
                                    if (activity != null)
                                        (activity as MainActivity).onBackToLogin()
                                })

                        } else {
                            try {
                                if (response.data!!.momentos.last().tipo == LoginResponse.Momentos.entrada) {
//                                Toasty.warning(act!!, resources.getString(R.string.ini_trabajo)) TODO
//                                    .show()

                                } else {
//                                Toasty.warning(act!!, resources.getString(R.string.fin_trabajo))
//                                    .show()
                                }

                                usuario!!.momentos = response.data.momentos
                                cargarAcciones(response.data.momentos, usuario!!.zonaHoraria!!)
                            } catch (e: Exception) {

                            }
                        }
                        activity?.let { it1 -> DialogHelper.getInstance().showLoadingAlert(it1, null, true) }
                        Handler(Looper.getMainLooper()).postDelayed({
                            activity?.let { it1 ->
                                DialogHelper.getInstance().showLoadingAlert(it1, null, false)
                            }
                        }, 1000)
                    }
                }
                Resource.Status.ERROR -> {
                    if (activity != null) {
                        DialogHelper.getInstance().showLoadingAlert(requireActivity(), null, false)
                        DialogHelper.getInstance().showOKAlert(activity = requireActivity(),
                            title = R.string.ups,
                            text = response.exception ?: ErrorHelper.addEventError,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {})
                    }
                }
            }
        }
    }

     */
}