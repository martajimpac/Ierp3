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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.BuildConfig
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.toools.ierp.R
import com.toools.ierp.core.*
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.Repository
import com.toools.ierp.data.model.LoginResponse
import com.toools.ierp.databinding.ActivityLoginBinding
import com.toools.ierp.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import java.util.*

const val TAG = "LoginActivity"

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    var fusedLocationClient: FusedLocationProviderClient? = null
    val fcmToken = FirebaseMessaging.getInstance().getToken()
    private val viewModel:  LoginViewModel by viewModels()

    var usuario: LoginResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpObservers()

        binding.apply{
            try {
                val pInfo = packageManager.getPackageInfo(packageName, 0) //TODO deprecated
                txtVersionApp.text =
                    String.format(getString(R.string.app_name_with_version), pInfo.versionName)

            } catch (e: PackageManager.NameNotFoundException) {

                if (BuildConfig.DEBUG)
                    e.printStackTrace()
                txtVersionApp.text = "${R.string.version_app}"
            }

            if (BuildConfig.DEBUG) {
                editClient.setText("TOOOLS")
                editUsuario.setText("martapracticas@toools.es")
                editPassword.setText("4rfv5tgb")

            }
        }

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


    override fun onResume() {
        super.onResume()

        ConstantHelper.clientREST = prefs.getString(ConstantHelper.client, "").toString()

        Handler(Looper.getMainLooper()).postDelayed({
            
            if (checkPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // TODO: buscar si el login es automatico
                activarLogin()
            }
        }, 200)
    }

    private fun activarLogin() {

        if (prefs.getBoolean(
                ConstantHelper.autoLogin,
                false
            ) && prefs.getString(ConstantHelper.usuarioLogin, null) != null
        ) {

            usuario = Gson().fromJson(
                prefs.getString(ConstantHelper.usuarioLogin, null),
                LoginResponse::class.java
            )

            //comprobar que el token firebase esta insertado
            if (!prefs.getBoolean(ConstantHelper.addTokenFirebase, false))

                usuario?.token?.let {
                    fcmToken.addOnSuccessListener(this@LoginActivity) { instanceIdResult ->
                        viewModel.addTokenFirebase(it, instanceIdResult)
                    }
                }

            if (usuario != null) {
                Repository.usuario = usuario //TODO NO SE SI ESTO HACE FALTA
                //consultar los momentos.
                DialogHelper.getInstance().showLoadingAlert(this, null, true)
                viewModel.momentos(usuario!!.token.let { usuario!!.token } ?: run { "" })
            }
        } else {
            DialogHelper.getInstance().showLoadingAlert(this, null, false)
        }

        binding.apply{
            btnEntrar.setOnClickListener {
                if (editPassword.text.toString().isBlank() || editPassword.text.toString()
                        .isBlank() || editClient.text.toString().isBlank()
                ) {
                    Toasty.warning(this@LoginActivity, R.string.alert_user, Toast.LENGTH_LONG).show()
                } else {
                    DialogHelper.getInstance().showLoadingAlert(this@LoginActivity, null, true)
                    viewModel.login(
                        editClient.text.toString(),
                        editUsuario.text.toString(),
                        editPassword.text.toString()
                    )
                }
            }
        }
    }

    fun setUpObservers() {

        //login
        viewModel.loginLiveData.observe(this){ response ->
            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(this, null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(this, null, false)

                    //Guardar el cliente en shared preferences
                    val edit = prefs.edit()
                    edit.putString(ConstantHelper.client, binding.editClient.text.toString())
                    edit.apply()

                    //mostrar mensaje de guardar session
                    DialogHelper.getInstance().showTwoButtonsAlert(this@LoginActivity,
                        resources.getString(R.string.title_aler_session),
                        resources.getString(R.string.desc_aler_session),
                        R.drawable.ic_toools_rellena,
                        resources.getString(R.string.yes),
                        completion1 = {
                            edit.putBoolean(ConstantHelper.autoLogin, true)
                            edit.putString(ConstantHelper.usuarioLogin, Gson().toJson(response.data))
                            edit.putBoolean(ConstantHelper.addTokenFirebase, false)
                            edit.apply()

                            response.data?.token?.let { token ->
                                //insertar el token de firebase
                                fcmToken.addOnCompleteListener(this@LoginActivity) { instanceIdResult ->
                                    viewModel.addTokenFirebase(token, instanceIdResult.toString())
                                    Log.e("TAG", token)
                                }
                            }

                            toMain(response.data!!)
                        },
                        button2 = resources.getString(R.string.no),
                        completion2 = {
                            toMain(response.data!!)
                        })
                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(this, null, false)

                    if (response.exception == ErrorHelper.notSession) {
                        DialogHelper.getInstance().showOKAlert(activity = this,
                            title = R.string.not_session,
                            text = R.string.desc_not_session,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {})

                    } else {
                        DialogHelper.getInstance().showOKAlert(activity = this,
                            title = R.string.error_login,
                            text = response.exception ?: ErrorHelper.loginError,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {})
                    }
                }
            }
        }

        //momentos
        viewModel.momentosLiveData.observe(this) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "momentos: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> {
                    //DialogHelper.getInstance().showLoadingAlert(this, null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(this, null, false)

                    usuario!!.momentos = response.data!!.momentos
                    toMain(usuario!!)

                }
                Resource.Status.ERROR -> {
                    DialogHelper.getInstance().showLoadingAlert(this, null, false)
                    if (response.data?.error == ErrorHelper.notSession) {
                        DialogHelper.getInstance().showOKAlert(activity = this,
                            title = R.string.not_session,
                            text = R.string.desc_not_session,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {})
                    } else {
                        DialogHelper.getInstance().showOKAlert(activity = this,
                            title = R.string.ups,
                            text = response.exception ?: ErrorHelper.momentosError,
                            icon = R.drawable.ic_toools_rellena,
                            completion = {})
                    }
                }
            }
        }

        //addTokenFirebase
        viewModel.momentosLiveData.observe(this) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "addTokenFirebase: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> { }
                Resource.Status.SUCCESS -> {
                    val edit = prefs.edit()
                    edit.putBoolean(ConstantHelper.addTokenFirebase, true)
                    edit.apply()
                }
                Resource.Status.ERROR -> { }
            }
        }
    }



    private fun toMain(usuario: LoginResponse) {

        cargarAlarmas()

        DialogHelper.getInstance().showLoadingAlert(this, null, true)

        Handler(Looper.getMainLooper()).postDelayed({

            DialogHelper.getInstance().showLoadingAlert(this, null, false)

            //Guardar el usuario
            Repository.usuario = usuario //TODO inject repository mejor

            val intent = Intent(this, MainActivity::class.java)

            binding.apply{

                val pairs: Array<androidx.core.util.Pair<View, String>> = arrayOf(
                    androidx.core.util.Pair(
                        imgIerp as View, resources.getString(R.string.trans_img_ierp)
                    )
                )

                val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity, *pairs)

                startActivity(intent, options.toBundle())
            }
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        if (prefs.getBoolean(ConstantHelper.sendNotificacion, true)) {

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
            editor.putBoolean(ConstantHelper.sendNotificacion, true)
            editor.putBoolean(ConstantHelper.horarioVerano, isVerano)
            editor.apply()

        } else {
            Alarms.getInstance().deleteAllAlarms(this)
        }
    } 
}