package com.toools.ierp.ui.login

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.core.ErrorHelper
import com.toools.ierp.core.Resource
import com.toools.ierp.core.prefs
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.Repository
import com.toools.ierp.data.model.LoginResponse
import com.toools.ierp.databinding.ActivityLoginBinding
import com.toools.ierp.ui.main.MainActivity
import com.toools.tooolsdialog.DialogHelper
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

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
                val pInfo: PackageInfo
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pInfo = packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0.toLong()))
                } else {
                    @Suppress("DEPRECATION")
                    pInfo = packageManager.getPackageInfo(packageName, 0)
                }
                txtVersionApp.text =
                    String.format(getString(R.string.app_name_with_version), pInfo.versionName)

            } catch (e: PackageManager.NameNotFoundException) {
                txtVersionApp.text = "${R.string.version_app}"
            }

            if (BuildConfig.DEBUG) {
                editClient.setText("toools")
                editUsuario.setText("pmarta@toools.es")
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
                activarLogin()
            }
        }, 200)
    }

    private fun activarLogin() {

        //si autologin es true y el usuario no es nulo
        if (prefs.getBoolean(ConstantHelper.autoLogin, false) &&
            prefs.getString(ConstantHelper.usuarioLogin, null) != null) {

            //recoge el usuario de shared prefs y lo transforma a loginResponse
            usuario = Gson().fromJson(
                prefs.getString(ConstantHelper.usuarioLogin, null),
                LoginResponse::class.java
            )

            //comprobar que el token firebase esta insertado y insertarlo si no lo esta
            if (!prefs.getBoolean(ConstantHelper.addTokenFirebase, false)){
                usuario?.token?.let {
                    fcmToken.addOnSuccessListener(this@LoginActivity) { instanceIdResult ->
                        viewModel.addTokenFirebase(it, instanceIdResult)
                    }
                }
            }

            DialogHelper.getInstance().showLoadingAlert(this, null, true)

            toMain(usuario!!)
        }

        binding.apply{

            //activar botón para mostrar o ocultar contraseña
            var passwordVisible = false
            btnShowPassword.setOnClickListener {
                passwordVisible = !passwordVisible
                if (passwordVisible) {
                    editPassword.transformationMethod = null
                    btnShowPassword.setImageResource(R.drawable.ic_eye_open)
                } else {
                    editPassword.transformationMethod = PasswordTransformationMethod()
                    btnShowPassword.setImageResource(R.drawable.ic_eye_closed)
                }
            }

            btnEntrar.setOnClickListener {
                if (editClient.text.toString().isBlank() || editUsuario.text.toString().isBlank() || editPassword.text.toString().isBlank()
                ) {
                    Toasty.warning(this@LoginActivity, R.string.alert_user, Toast.LENGTH_LONG).show()
                } else {
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

            if (BuildConfig.DEBUG) {
                Log.e(TAG,"login: {${response.status}}")
            }

            when (response.status) {
                Resource.Status.LOADING -> {
                    DialogHelper.getInstance().showLoadingAlert(this, null, true)
                }
                Resource.Status.SUCCESS -> {
                    DialogHelper.getInstance().showLoadingAlert(this, null, false)

                    usuario = response.data

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
                            edit.putString(ConstantHelper.usuarioLogin, Gson().toJson(usuario))
                            edit.putBoolean(ConstantHelper.addTokenFirebase, false)
                            edit.apply()

                            usuario?.token?.let { token ->
                                //insertar el token de firebase
                                fcmToken.addOnCompleteListener(this@LoginActivity) { instanceIdResult ->
                                    viewModel.addTokenFirebase(token, instanceIdResult.toString())
                                }
                            }
                            toMain(usuario!!)
                        },
                        button2 = resources.getString(R.string.no),
                        completion2 = {
                            edit.putBoolean(ConstantHelper.autoLogin, false)
                            edit.putString(ConstantHelper.usuarioLogin, Gson().toJson(usuario))
                            edit.apply()
                            toMain(usuario!!)
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
        /*viewModel.momentosLiveData.observe(this) { response ->
            if (BuildConfig.DEBUG)
                Log.e(TAG, "momentos: {${response.status}}")
            when (response.status) {
                Resource.Status.LOADING -> { }
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
        }*/

        //addTokenFirebase
        viewModel.addTokenFirebaseLiveData.observe(this) { response ->
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "addTokenFirebase: {${response.status}}")
            }

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

            DialogHelper.getInstance().showLoadingAlert(this@LoginActivity, null, false)

            Repository.usuario = usuario

            val intent = Intent(this, MainActivity::class.java)

            binding.apply{

                val pairs: Array<androidx.core.util.Pair<View, String>> = arrayOf(
                    androidx.core.util.Pair(
                        imgIerp as View, resources.getString(R.string.trans_img_ierp)
                    )
                )

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity, *pairs)
                startActivity(intent, options.toBundle())
            }
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
}