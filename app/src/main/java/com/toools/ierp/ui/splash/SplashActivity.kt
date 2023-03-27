package com.toools.ierp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.viewbinding.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.databinding.ActivityLoginBinding
import com.toools.ierp.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import com.toools.ierp.core.prefs
import com.toools.ierp.databinding.ActivitySplashBinding

const val TAG = "SplashActivity"

//TODO QUE SIGNIFICA SUPPRESS LINT
@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel:  SplashViewModel by viewModels()

    private var intentosIspot: Int = 0
    private lateinit var tokenFirebase: String
    private var isLoad: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.addUserRecived.observe(this, addUserObserver)

        binding.apply{
            try {
                val pInfo = packageManager.getPackageInfo(packageName, 0)
                txtVersionApp.text =
                    String.format(getString(R.string.app_name_with_version), pInfo.versionName)

            } catch (e: PackageManager.NameNotFoundException) {

                if (BuildConfig.DEBUG)
                    e.printStackTrace()
                txtVersionApp.text = "Version de la app" //TODO: HACER QUE ESTO NO SEA UN STRING LITERAL
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            delay(1500)
            val fcmToken = FirebaseMessaging.getInstance().getToken()
            fcmToken.addOnSuccessListener(this@SplashActivity) { instanceIdResult ->
                tokenFirebase = instanceIdResult
                Log.e("tokenFirebase", tokenFirebase)

                if (prefs.getString(
                        ConstantHelper.registeredFirebaseToken,
                        null
                    ) != instanceIdResult
                )
                    viewModel.callAddUserFirebase(tokenFirebase)
            }

            isLoad = false

            toLogin()
        }
    }

    private fun toLogin() {

        val intent = Intent(this, LoginActivity::class.java)

        binding.apply{
            val pairs: Array<Pair<View, String>> = arrayOf(
                Pair(imgIerp as View, resources.getString(R.string.trans_img_ierp)),
                Pair(imgToools as View, resources.getString(R.string.trans_img_toools)),
                Pair(txtVersionApp as View, resources.getString(R.string.trans_text_app))
            )

            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this@SplashActivity, *pairs)

            startActivity(intent, options.toBundle())
        }
    }

    private val addUserObserver = Observer<Resource<String>> { resource ->

        if (BuildConfig.DEBUG)
            Log.e(TAG, "addUser: {${resource.status}}")

        when (resource.status) {

            Resource.Status.LOADING -> {

            }
            Resource.Status.SUCCESS -> {

                val editor = prefs.edit()
                editor.putString(ConstantsHelper.registeredFirebaseToken, resource.data)
                editor.apply()

            }
            Resource.Status.ERROR -> {
                if (intentosIspot < 3 && !tokenFirebase.isEmpty())
                    viewModel.callAddUserFirebase(tokenFirebase)
                intentosIspot++
            }
        }
    }

    fun setUpObservers() {
        viewModel.get
        mainViewModel.getMenuClienteLiveData.observe(this) {
            when (it.status) {
                Resource.Status.LOADING -> {}
                Resource.Status.SUCCESS -> {
                    it.data?.let { response ->
                        if (response.isOK()) {
                            listEventos = response.clientes
                            loadMenuDerecho()
                        } else {
                            //TODO mostrar error obtener datos eventos
                        }
                    }
                }
                Resource.Status.ERROR -> {}
            }
        }
    }
}