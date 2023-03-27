package com.toools.ierp.ui.login


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.BuildConfig
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.toools.ierp.R
import com.toools.ierp.alarm.Alarms
import com.toools.ierp.entities.Resource
import com.toools.ierp.entities.RestBaseObject
import com.toools.ierp.entities.ierp.LoginResponse
import com.toools.ierp.entities.ierp.MomentosResponse
import com.toools.ierp.helpers.ConstantsHelper
import com.toools.ierp.helpers.DialogHelper
import com.toools.ierp.helpers.prefs
import com.toools.ierp.helpers.rest.ErrorHelper
import com.toools.ierp.helpers.rest.RestConstants
import com.toools.ierp.helpers.rest.RestRepository
import com.toools.ierp.ui.main.MainActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    var fusedLocationClient: FusedLocationProviderClient? = null
    val fcmToken = FirebaseMessaging.getInstance().getToken()
    private val viewModel: LoginViewModel by lazy {

        ViewModelProvider(this, this.defaultViewModelProviderFactory)[LoginViewModel::class.java]
    }

    var usuario: LoginResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        try {

            val pInfo = packageManager.getPackageInfo(packageName, 0)
            txtVersionApp.text =
                String.format(getString(R.string.app_name_with_version), pInfo.versionName)

        } catch (e: PackageManager.NameNotFoundException) {

            if (BuildConfig.DEBUG)
                e.printStackTrace()
            txtVersionApp.text = "Version de la app"
        }


        if (BuildConfig.DEBUG) {
              editClient.setText("TOOOLS")
              editUsuario.setText("mmunoz@toools.es")
              editPassword.setText("4rfv5tgb")

        }

        viewModel.loginIerpRecived.observe(this, loginObserver)
        viewModel.momentosRecived.observe(this, momentosObserver)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if (checkPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                //buscar si el login es automatico
                activarLogin()
            }
        }, 200)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        RestConstants.client = prefs.getString(ConstantsHelper.client, "").toString()

        Handler(Looper.getMainLooper()).postDelayed({
            if (checkPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                //buscar si el login es automatico
                activarLogin()
            }
        }, 200)
    }

    private fun activarLogin() {

        if (prefs.getBoolean(
                ConstantsHelper.autoLogin,
                false
            ) && prefs.getString(ConstantsHelper.usuarioLogin, null) != null
        ) {

            usuario = Gson().fromJson(
                prefs.getString(ConstantsHelper.usuarioLogin, null),
                LoginResponse::class.java
            )

            //comprobar que el token firebase esta insertado
            if (!prefs.getBoolean(ConstantsHelper.addTokenFirebase, false))

                usuario?.token?.let {
                    fcmToken.addOnSuccessListener(this@LoginActivity) { instanceIdResult ->
                        viewModel.sendTokenFirebase(it, instanceIdResult)
                    }
                }

            if (usuario != null) {
                RestRepository.getInstance().usuario = usuario
                //consultar los momentos.
                DialogHelper.getInstance().showLoadingAlert(this, null, true)
                viewModel.callMomentos(usuario!!.token.let { usuario!!.token } ?: run { "" })
            }
        } else {
            DialogHelper.getInstance().showLoadingAlert(this, null, false)
        }

        btnEntrar.setOnClickListener {
            if (editPassword.text.toString().isBlank() || editPassword.text.toString()
                    .isBlank() || editClient.text.toString().isBlank()
            ) {
                Toasty.warning(this@LoginActivity, R.string.alert_user, Toast.LENGTH_LONG).show()
            } else {
                DialogHelper.getInstance().showLoadingAlert(this, null, true)
                viewModel.callLoginIerp(
                    editClient.text.toString(),
                    editUsuario.text.toString(),
                    editPassword.text.toString()
                )
            }
        }
    }

    private val loginObserver = Observer<Resource<LoginResponse>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(TAG, "loginUser: {${resource.status}}")
        when (resource.status) {

            Resource.Status.LOADING -> {
                DialogHelper.getInstance().showLoadingAlert(this, null, true)
            }
            Resource.Status.SUCCESS -> {
                DialogHelper.getInstance().showLoadingAlert(this, null, false)

                val edit = prefs.edit()
                edit.putString(ConstantsHelper.client, editClient.text.toString())
                edit.apply()

                //mostrar mensaje de guardar session
                DialogHelper.getInstance().showTwoButtonsAlert(this@LoginActivity,
                    resources.getString(R.string.title_aler_session),
                    resources.getString(R.string.desc_aler_session),
                    R.drawable.ic_toools_rellena,
                    resources.getString(R.string.yes),
                    completion1 = {
                        val edit = prefs.edit()
                        edit.putBoolean(ConstantsHelper.autoLogin, true)
                        edit.putString(ConstantsHelper.usuarioLogin, Gson().toJson(resource.data))
                        edit.putBoolean(ConstantsHelper.addTokenFirebase, false)
                        edit.apply()

                        resource.data?.token?.let { token ->
                            //insertar el token de firebase
                            fcmToken.addOnCompleteListener(this@LoginActivity) { instanceIdResult ->
                                viewModel.sendTokenFirebase(token, instanceIdResult.toString())
                                Log.e("TAG", token)
                            }
                        }

                        goToMain(resource.data!!)
                    },
                    button2 = resources.getString(R.string.no),
                    completion2 = {
                        goToMain(resource.data!!)
                    })
            }
            Resource.Status.ERROR -> {
                DialogHelper.getInstance().showLoadingAlert(this, null, false)

                if (resource.data?.error != null &&
                    Integer.parseInt(resource.data.error) == ErrorHelper.SESSION_EXPIRED
                ) {

                    DialogHelper.getInstance().showOKAlert(activity = this,
                        title = R.string.not_session,
                        text = R.string.desc_not_session,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {

                        })

                } else {
                    DialogHelper.getInstance().showOKAlert(activity = this,
                        title = R.string.error_login,
                        text = resource.exception?.message() ?: ErrorHelper.loginError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {

                        })
                }
            }
        }
    }

    private val momentosObserver = Observer<Resource<MomentosResponse>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(TAG, "momentos: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {
                //DialogHelper.getInstance().showLoadingAlert(this, null, true)
            }
            Resource.Status.SUCCESS -> {
                DialogHelper.getInstance().showLoadingAlert(this, null, false)

                usuario!!.momentos = resource.data!!.momentos
                goToMain(usuario!!)

            }
            Resource.Status.ERROR -> {
                DialogHelper.getInstance().showLoadingAlert(this, null, false)
                if (resource.data?.error != null &&
                    Integer.parseInt(resource.data.error) == ErrorHelper.SESSION_EXPIRED
                ) {

                    DialogHelper.getInstance().showOKAlert(activity = this,
                        title = R.string.not_session,
                        text = R.string.desc_not_session,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {

                        })

                } else {
                    DialogHelper.getInstance().showOKAlert(activity = this,
                        title = R.string.ups,
                        text = resource.exception?.message() ?: ErrorHelper.momentosError,
                        icon = R.drawable.ic_toools_rellena,
                        completion = {

                        })
                }
            }
        }
    }

    private val addTokenFirebaseObserver = Observer<Resource<RestBaseObject>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(TAG, "addTokenFirebase: {${resource.status}}")
        when (resource.status) {
            Resource.Status.LOADING -> {

            }
            Resource.Status.SUCCESS -> {
                val edit = prefs.edit()
                edit.putBoolean(ConstantsHelper.addTokenFirebase, true)
                edit.apply()
            }
            Resource.Status.ERROR -> {

            }
        }
    }

    private fun goToMain(usuario: LoginResponse) {

        cargarAlarmas()

        DialogHelper.getInstance().showLoadingAlert(this, null, true)

        Handler(Looper.getMainLooper()).postDelayed({

            DialogHelper.getInstance().showLoadingAlert(this, null, false)

            RestRepository.getInstance().usuario = usuario

            val pairs: Array<androidx.core.util.Pair<View, String>> = arrayOf(
                androidx.core.util.Pair(
                    imgIerp as View,
                    resources.getString(R.string.trans_img_ierp)
                )
            )

            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity, *pairs)

            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent, options.toBundle())

        }, 500)
    }


    private val PERMISSION_ID = 42
    private fun checkPermission(vararg perm: String): Boolean {
        val havePermissions = perm.toList().all {
            ContextCompat.checkSelfPermission(this, it) ==
                    PackageManager.PERMISSION_GRANTED
        }
        if (!havePermissions) {
            if (perm.toList()
                    .any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }
            ) {
                val dialog = AlertDialog.Builder(this)
                    .setTitle(resources.getString(R.string.titu_localizacion))
                    .setMessage(resources.getString(R.string.desc_localizacion))
                    .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                        ActivityCompat.requestPermissions(this, perm, PERMISSION_ID)
                    }
                    .setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                        checkPermission(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }
                    .create()
                dialog.show()
            } else {
                ActivityCompat.requestPermissions(this, perm, PERMISSION_ID)
            }
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_ID -> {

                val havePermissions = permissions.toList().all {
                    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
                }

                if (havePermissions) {
                    activarLogin()
                } else {
                    checkPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            }
        }
    }

    fun cargarAlarmas() {
        if (prefs.getBoolean(ConstantsHelper.sendNotificacion, true)) {

            //comprobar si es horario de verano o no
            val now = Calendar.getInstance()
            val junio = Calendar.getInstance()
            junio.set(now.get(Calendar.YEAR), 5, 15)
            val septiembre = Calendar.getInstance()
            septiembre.set(now.get(Calendar.YEAR), 8, 15)

            var isVerano = false
            if (now.after(junio) && now.before(septiembre)) {
                isVerano = true
            }

            Alarms.getInstance().createAlarmn(this, isVerano)

            val editor = prefs.edit()
            editor.putBoolean(ConstantsHelper.sendNotificacion, true)
            editor.putBoolean(ConstantsHelper.horarioVerano, isVerano)
            editor.apply()

        } else {
            Alarms.getInstance().deleteAllAlarms(this)
        }
    }
}