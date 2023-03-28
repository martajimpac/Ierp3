package com.toools.ierp.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.viewbinding.BuildConfig
import com.google.firebase.FirebaseApp
import com.toools.ierp.R
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import com.toools.ierp.core.Resource
import com.toools.ierp.core.prefs
import com.toools.ierp.databinding.ActivitySplashBinding

const val TAG = "SplashActivity"
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
        setUpObservers()

        binding.apply{
            try {
                val pInfo = packageManager.getPackageInfo(packageName, 0) //todo quitar deprecated
                txtVersionApp.text =
                    String.format(getString(R.string.app_name_with_version), pInfo.versionName)

            } catch (e: PackageManager.NameNotFoundException) {

                if (BuildConfig.DEBUG)
                    e.printStackTrace()
                txtVersionApp.text = "${R.string.version_app}"
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
                    viewModel.addUser(tokenFirebase)
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


    fun setUpObservers() {
        viewModel.addUserLiveData.observe(this){ response ->

            when (response.status) {
                Resource.Status.LOADING -> {}

                Resource.Status.SUCCESS -> {
                    //pasar el token a login por shared preferences
                    val editor = prefs.edit()
                    editor.putString(ConstantHelper.registeredFirebaseToken, tokenFirebase)
                    editor.apply()
                }
                Resource.Status.ERROR -> {
                    //si falla intentamos llamar otra vez, al tercer intento ya no llamamos más
                    if (intentosIspot < 3 && !tokenFirebase.isEmpty())
                        viewModel.addUser(tokenFirebase)
                    intentosIspot++
                }
            }
        }
    }
}