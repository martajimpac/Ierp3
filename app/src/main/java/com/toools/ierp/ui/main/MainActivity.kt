package com.toools.ierp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.toools.ierp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

/*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.app.ActivityOptionsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.core.Alarms
import com.toools.ierp.helpers.ConstantsHelper
import com.toools.ierp.helpers.DialogHelper
import com.toools.ierp.helpers.prefs
import com.toools.ierp.helpers.rest.RestConstants
import com.toools.ierp.helpers.rest.RestRepository
import com.toools.ierp.ui.base.BaseActivity
import com.toools.ierp.ui.login.LoginActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_left.*
import java.util.*


const val TAG = "MainActivity"

class MainActivity : BaseActivity(), SeccionesListener {

    private var isPaused: Boolean = false
    private var adapterSecciones: AdapterSecciones? = null

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, this.defaultViewModelProviderFactory)[MainViewModel::class.java]
    }

    override fun onBackPressed() {
        if (contentFragment.findNavController().currentDestination?.id == R.id.fichajeFragment) {
            moveTaskToBack(true)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        RestConstants.client = prefs.getString(ConstantsHelper.client, "").toString()
    }

    var toggle: ActionBarDrawerToggle? = null
    var showingBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        backImageIERP()
        navigationLeft.background = getDrawable(R.color.transparente)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        toggle?.let { togle ->

            drawerLayout.addDrawerListener(togle)

            togle.syncState()

            toolbar.navigationIcon = DrawerArrowDrawable(this)
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
            else {

                if (drawerLayout.isDrawerOpen(navigationLeft))
                    drawerLayout.closeDrawer(navigationLeft)
                else
                    drawerLayout.openDrawer(navigationLeft)
            }
        }

        imageView6.setOnClickListener {
            byTooolsClickListener()
        }

        cardOut.setOnClickListener {
            logOut()
        }

        //cargar los datos del empleado en el drawer
        RestRepository.getInstance().usuario?.let { usuario ->
            nombreUsuarioTextView.text = usuario.nombre

            Glide.with(this).load(resources.getString(R.string.url_base_img, usuario.username))
                .circleCrop()
                .error(
                    Glide.with(this).load(R.drawable.luciano).circleCrop()
                ).into(imgUsuario)
        } ?: run {
            onBackToLogin()
        }

        try {

            val pInfo = packageManager.getPackageInfo(packageName, 0)
            appVersionTextView.text =
                String.format(getString(R.string.app_name_with_version), pInfo.versionName)

        } catch (e: PackageManager.NameNotFoundException) {

            if (BuildConfig.DEBUG)
                e.printStackTrace()
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerSecciones.layoutManager = layoutManager
        recyclerSecciones.setHasFixedSize(true)
        (recyclerSecciones.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        adapterSecciones = AdapterSecciones(
            this, ConstantsHelper.Seccion.values().toMutableList(),
            prefs.getBoolean(ConstantsHelper.sendNotificacion, true), this
        )

        recyclerSecciones.adapter = adapterSecciones
    }

    fun backImageIERP() {
        imgIerp.setOnClickListener {
            contentFragment.findNavController().navigate(R.id.fichajeFragment)
            /*contentFragment.findNavController().currentDestination?.let {
                if (it.id == R.id.fichajeFragment) {
                    moveTaskToBack(true)
                }
            }*/
//            super.onBackPressed()
        }
    }

    fun showIconBack(show: Boolean) {
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

            scale = (1.5f - slideOffset).coerceAtLeast(1.0f)

            tooolsContent.visibility = View.VISIBLE

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

            tooolsContent.scaleX = 2.25f - slideOffset
            tooolsContent.scaleY = 2.25f - slideOffset
            tooolsContent.alpha = slideOffset + 0.1f

        }

        override fun onDrawerOpened(drawer: View) {}

        override fun onDrawerClosed(drawer: View) {}

        override fun onDrawerStateChanged(newState: Int) {}
    }

    fun logOut() {
        val editor = prefs.edit()
        editor.putBoolean(ConstantsHelper.autoLogin, false)
        editor.putString(ConstantsHelper.usuarioLogin, null)
        editor.apply()

        onBackToLogin()
    }

    fun onBackToLogin() {

        val pairs: Array<androidx.core.util.Pair<View, String>> = arrayOf(
            androidx.core.util.Pair(imgIerp as View, resources.getString(R.string.trans_img_ierp))
        )

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity, *pairs)

        val intent = Intent(this, LoginActivity::class.java)

        startActivity(intent, options.toBundle())
    }

    //*****************
    //Secciones Listener Metodos
    //*****************
    override fun selectedSeccion(seccion: ConstantsHelper.Seccion) {

        contentFragment.findNavController().let { navController ->

            var id: Int = R.id.fichajeFragment

            when (seccion) {
                ConstantsHelper.Seccion.fichajes -> {
                    id = R.id.fichajeFragment
                }
                ConstantsHelper.Seccion.guardias -> {
                    id = R.id.guardiasFragment
                }
                ConstantsHelper.Seccion.proyectos -> {
                    id = R.id.proyectosFragment
                }
                ConstantsHelper.Seccion.tareas -> {
                    id = R.id.tareasFragment
                }
                ConstantsHelper.Seccion.tareasAsignadas -> {
                    id = R.id.tareasAsignadasFragment
                }
                ConstantsHelper.Seccion.soportes -> {
                    id = R.id.soportesFragment
                }
                ConstantsHelper.Seccion.notificaciones -> {
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

    override fun changeNotificaciones(isNotificacion: Boolean) {

        if (prefs.getBoolean(ConstantsHelper.sendNotificacion, true)) {
            DialogHelper.getInstance().showTwoButtonsAlert(
                this@MainActivity, resources.getString(R.string.title_aler_notifi),
                resources.getString(R.string.desc_aler_notifi),
                R.drawable.ic_toools_rellena,
                resources.getString(R.string.yes),
                completion1 = {
                    val editor = prefs.edit()
                    editor.putBoolean(ConstantsHelper.sendNotificacion, false)
                    editor.apply()
                    Toasty.success(this, resources.getString(R.string.notifi_off)).show()
                    Alarms.getInstance().deleteAllAlarms(this)

                    adapterSecciones?.let {
                        it.setNotificaciones(false)
                        recyclerSecciones.adapter = it
                    }
                },
                button2 = resources.getString(R.string.no),
                completion2 = {
                    adapterSecciones?.let {
                        it.setNotificaciones(
                            prefs.getBoolean(
                                ConstantsHelper.sendNotificacion,
                                true
                            )
                        )
                        recyclerSecciones.adapter = it
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

            Alarms.getInstance().createAlarmn(this, isVerano)

            val editor = prefs.edit()
            editor.putBoolean(ConstantsHelper.sendNotificacion, true)
            editor.putBoolean(ConstantsHelper.horarioVerano, isVerano)
            editor.apply()

            adapterSecciones?.let {
                it.setNotificaciones(true)
                recyclerSecciones.adapter = it
            }
        }
    }

    companion object {
        lateinit var app: MainActivity
        fun getInstance(): MainActivity {
            return app
        }
    }
}*/