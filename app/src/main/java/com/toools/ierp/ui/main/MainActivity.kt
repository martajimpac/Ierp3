package com.toools.ierp.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.app.ActivityOptionsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.core.DialogHelper
import com.toools.ierp.core.prefs
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.LoginResponse
import com.toools.ierp.databinding.ActivityMainBinding
import com.toools.ierp.databinding.NavigationLeftBinding
import com.toools.ierp.ui.base.BaseActivity
import com.toools.ierp.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import java.util.*


const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : BaseActivity(), SeccionesListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationBinding: NavigationLeftBinding
    private var adapterSecciones: AdapterSecciones? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navigationBinding = NavigationLeftBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            backImageIERP()
            navigationLeft.background = getDrawable(R.color.transparente)

            toggle = ActionBarDrawerToggle(
                this@MainActivity,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
            )
            toggle?.let { togle ->

                drawerLayout.addDrawerListener(togle)

                togle.syncState()

                toolbar.navigationIcon = DrawerArrowDrawable(this@MainActivity)
                togle.isDrawerIndicatorEnabled = false
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                togle.isDrawerIndicatorEnabled = true

                drawerLayout.setScrimColor(Color.TRANSPARENT)
                drawerLayout.addDrawerListener(simpleDrawerListener)
            }

            toolbar.setNavigationOnClickListener {
                if (showingBack)
                    onBackPressed()
                    //requireView().findNavController().popBackStack() //TODO ver si funciona
                else {
                    if (drawerLayout.isDrawerOpen(navigationLeft))
                        drawerLayout.closeDrawer(navigationLeft)
                    else
                        drawerLayout.openDrawer(navigationLeft)
                }
            }

            navigationBinding.imageView6.setOnClickListener { byTooolsClickListener() }

            cardOut.setOnClickListener {
                logOut()
            }
        }

        navigationBinding.apply{
            var usuario: LoginResponse? = Gson().fromJson(
                prefs.getString(ConstantHelper.usuarioLogin, null),
                LoginResponse::class.java
            )

            usuario?.let {
                nombreUsuarioTextView.text = usuario.nombre

                Glide.with(this@MainActivity).load(resources.getString(R.string.url_base_img, usuario.username))
                    .circleCrop()
                    .error(
                        Glide.with(this@MainActivity).load(R.drawable.luciano).circleCrop()
                    ).into(imgUsuario)
            } ?: run {
                onBackToLogin()
            }

            try {

                //todo deprecated
                val pInfo = packageManager.getPackageInfo(packageName, 0)
                appVersionTextView.text = String.format(getString(R.string.app_name_with_version), pInfo.versionName)

            } catch (e: PackageManager.NameNotFoundException) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace()
            }

            val layoutManager = LinearLayoutManager(this@MainActivity)
            layoutManager.orientation = RecyclerView.VERTICAL

            recyclerSecciones.layoutManager = layoutManager
            recyclerSecciones.setHasFixedSize(true)
            (recyclerSecciones.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            adapterSecciones = AdapterSecciones(
                this@MainActivity, ConstantHelper.Seccion.values().toMutableList(),
                prefs.getBoolean(ConstantHelper.sendNotificacion, true), this@MainActivity
            )

            recyclerSecciones.adapter = adapterSecciones
        }
    }

    override fun onResume() {
        super.onResume()
        ConstantHelper.clientREST = prefs.getString(ConstantHelper.client, "").toString() //todo, porque hay que hacer esto en todos los on resume?
    }

    var toggle: ActionBarDrawerToggle? = null
    var showingBack = false

    fun backImageIERP() {
        binding.apply{
            imgIerp.setOnClickListener {
                contentFragment.findNavController().navigate(R.id.fichajeFragment)
                contentFragment.findNavController().currentDestination?.let {
                    if (it.id == R.id.fichajeFragment) {
                        moveTaskToBack(true)
                    }
                }
                super.onBackPressed()
                //todo deprecated
            }
        }
    }

    fun showIconBack(show: Boolean) { //todo quitar
        if (show) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        showingBack = show
    }

    //DRAWER LAYOUT
    private val simpleDrawerListener = object : DrawerLayout.SimpleDrawerListener() {

        var scale: Float = 0.0f
        override fun onDrawerSlide(drawer: View, slideOffset: Float) {

            navigationBinding.apply{
                scale = (1.5f - slideOffset).coerceAtLeast(1.0f)

                tooolsContent.visibility = View.VISIBLE

                binding.apply{
                    content.x = navigationLeft.width * slideOffset
                    val lp = content.layoutParams as DrawerLayout.LayoutParams
                    lp.height =
                        drawerLayout.height - (drawerLayout.height.toFloat() * slideOffset * 0.28f).toInt()
                    lp.topMargin = (drawerLayout.height - lp.height) / 2
                    lp.width =
                        drawerLayout.width - (drawerLayout.width.toFloat() * slideOffset * 0.28f).toInt()
                    content.layoutParams = lp

                    navigationLeft.scaleX = scale
                    navigationLeft.scaleY = scale
                    navigationLeft.alpha = slideOffset + 0.1f
                }

                tooolsContent.scaleX = 2.25f - slideOffset
                tooolsContent.scaleY = 2.25f - slideOffset
                tooolsContent.alpha = slideOffset + 0.1f

            }
        }

        override fun onDrawerOpened(drawer: View) {}

        override fun onDrawerClosed(drawer: View) {}

        override fun onDrawerStateChanged(newState: Int) {}

    }

    fun logOut() {
        val editor = prefs.edit()
        editor.putBoolean(ConstantHelper.autoLogin, false)
        editor.putString(ConstantHelper.usuarioLogin, null)
        editor.apply()

        onBackToLogin()
    }

    fun onBackToLogin() {
        binding.apply {
            val pairs: Array<androidx.core.util.Pair<View, String>> = arrayOf(
                androidx.core.util.Pair(imgIerp as View, resources.getString(R.string.trans_img_ierp))
            )

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity, *pairs)

            val intent = Intent(this@MainActivity, LoginActivity::class.java)

            startActivity(intent, options.toBundle())
        }
    }

    //*****************
    //Secciones Listener Metodos
    //*****************
    override fun selectedSeccion(seccion: ConstantHelper.Seccion) {
        binding.apply{

            contentFragment.findNavController().let { navController ->

                var id: Int = R.id.fichajeFragment

                when (seccion) {
                    ConstantHelper.Seccion.fichajes -> {
                        id = R.id.fichajeFragment
                    }
                    ConstantHelper.Seccion.guardias -> {
                        id = R.id.guardiasFragment
                    }
                    ConstantHelper.Seccion.proyectos -> {
                        id = R.id.proyectosFragment
                    }
                    ConstantHelper.Seccion.tareas -> {
                        id = R.id.tareasFragment
                    }
                    ConstantHelper.Seccion.tareasAsignadas -> {
                        id = R.id.tareasAsignadasFragment
                    }
                    ConstantHelper.Seccion.soportes -> {
                        id = R.id.soportesFragment
                    }
                    ConstantHelper.Seccion.notificaciones -> {
                    }
                }

                drawerLayout.closeDrawer(navigationLeft)

                navController.currentDestination?.let {
                    if (it.id != id) {
                        navController.navigate(id)
                    }
                } ?: run {
                    navController.navigate(id)
                }
            }
        }
    }

    override fun changeNotificaciones(isNotificacion: Boolean) {

        if (prefs.getBoolean(ConstantHelper.sendNotificacion, true)) {
            DialogHelper.getInstance().showTwoButtonsAlert(
                this@MainActivity, resources.getString(R.string.title_aler_notifi),
                resources.getString(R.string.desc_aler_notifi),
                R.drawable.ic_toools_rellena,
                resources.getString(R.string.yes),
                completion1 = {
                    val editor = prefs.edit()
                    editor.putBoolean(ConstantHelper.sendNotificacion, false)
                    editor.apply()
                    Toasty.success(this, resources.getString(R.string.notifi_off)).show()

                    adapterSecciones?.let {
                        it.setNotificaciones(false)
                        navigationBinding.recyclerSecciones.adapter = it
                    }
                },
                button2 = resources.getString(R.string.no),
                completion2 = {
                    adapterSecciones?.let {
                        it.setNotificaciones(
                            prefs.getBoolean(
                                ConstantHelper.sendNotificacion,
                                true
                            )
                        )
                        navigationBinding.recyclerSecciones.adapter = it
                    }
                })
        } else {

            Toasty.success(this, resources.getString(R.string.notifi_on)).show()

            val now = Calendar.getInstance()
            val junio = Calendar.getInstance()
            junio.set(now.get(Calendar.YEAR), 5, 15)
            val septiembre = Calendar.getInstance()
            septiembre.set(now.get(Calendar.YEAR), 8, 15)

            var isVerano = false
            if (now.after(junio) && now.before(septiembre)) {
                isVerano = true
            }

            val editor = prefs.edit()
            editor.putBoolean(ConstantHelper.sendNotificacion, true)
            editor.putBoolean(ConstantHelper.horarioVerano, isVerano)
            editor.apply()

            adapterSecciones?.let {
                it.setNotificaciones(true)
                navigationBinding.recyclerSecciones.adapter = it
            }
        }
    }

    companion object { //TODO CAMBIAR ESTO POR UN INJECT o ver si se puede quitar
        lateinit var app: MainActivity
        fun getInstance(): MainActivity {
            return app
        }
    }
}