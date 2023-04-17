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
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.core.DialogHelper
import com.toools.ierp.core.prefs
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.Repository
import com.toools.ierp.databinding.ActivityMainBinding
import com.toools.ierp.ui.base.BaseActivity
import com.toools.ierp.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import java.util.*

const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : BaseActivity(), MenuListener {

    private lateinit var binding: ActivityMainBinding
    private var adapterMenu: AdapterMenu? = null

    var toggle: ActionBarDrawerToggle? = null
    var showingBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpView()
    }

    override fun onResume() {
        super.onResume()
        ConstantHelper.clientREST = prefs.getString(ConstantHelper.client, "").toString()
    }

    fun setUpView() {

        binding.apply {

            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            backImageIERP()

            navigationView.background = getDrawable(R.color.transparente)

            //crear icono para mostrar y ocultar el menu de navegacion
            toggle = ActionBarDrawerToggle(
                this@MainActivity,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
            )

            toggle?.let { toggle ->
                //añadir el toggle como listener
                drawerLayout.addDrawerListener(toggle)
                toggle.syncState()

                //para que el simbolo de abrir menu se convierta en una flecha
                toolbar.navigationIcon = DrawerArrowDrawable(this@MainActivity)

                //para que aparezca el simbolo de abrir menu
                toggle.isDrawerIndicatorEnabled = false
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                toggle.isDrawerIndicatorEnabled = true
            }

            //para que la pantalla se quede del mismo color al abrir el menu
            drawerLayout.setScrimColor(Color.TRANSPARENT)

            //agregar el listener al drawerlayout
            drawerLayout.addDrawerListener(simpleDrawerListener)

            //abre y cierra el drawer al pulsar el icono
            toolbar.setNavigationOnClickListener {
                if (showingBack)
                    onBackPressedDispatcher.onBackPressed()
                else {
                    if (drawerLayout.isDrawerOpen(navigationView)) {
                        drawerLayout.closeDrawer(navigationView)
                    } else {
                        drawerLayout.openDrawer(navigationView)
                    }
                }
            }

            menuLayout.byTooolsImageView.setOnClickListener {
                byTooolsClickListener()
            }

            cardOut.setOnClickListener {
                logOut()
            }

            //cargar los datos del usuario en el drawer

            Repository.usuario?.let { usuario ->
                menuLayout.nombreUsuarioTextView.text = usuario.nombre
                Glide.with(this@MainActivity)
                    .load(resources.getString(R.string.url_base_img, usuario.username))
                    .circleCrop()
                    .error(
                        Glide.with(this@MainActivity).load(R.drawable.luciano).circleCrop()
                    ).into(menuLayout.imgUsuario)
            } ?: run {
                onBackToLogin()
            }

            //añadir los datos de la version
            try {
                val pInfo = packageManager.getPackageInfo(packageName, 0)
                menuLayout.appVersionTextView.text =
                    String.format(getString(R.string.app_name_with_version), pInfo.versionName)

            } catch (e: PackageManager.NameNotFoundException) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace()
            }
            menuLayout.apply {
                //añadir el adapter
                val layoutManager = LinearLayoutManager(this@MainActivity)
                layoutManager.orientation = RecyclerView.VERTICAL
                recyclerMenu.layoutManager = layoutManager
                recyclerMenu.setHasFixedSize(true)
                (recyclerMenu.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                    false

                adapterMenu = AdapterMenu(
                    this@MainActivity, ConstantHelper.SeccionMenu.values().toMutableList(),
                    prefs.getBoolean(ConstantHelper.sendNotificacion, true), this@MainActivity
                )

                recyclerMenu.adapter = adapterMenu
            }

        }
    }

    //a esta funcion se la llama desde otros fragment
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

            binding.apply{
                scale = (1.5f - slideOffset).coerceAtLeast(1.0f)

                //mostrar logo de abajo
                menuLayout.tooolsContent.visibility = View.VISIBLE

                content.x = navigationView.width * slideOffset
                val lp = content.layoutParams as DrawerLayout.LayoutParams
                lp.height =
                    drawerLayout.height - (drawerLayout.height.toFloat() * slideOffset * 0.28f).toInt()
                lp.topMargin = (drawerLayout.height - lp.height) / 2
                lp.width =
                    drawerLayout.width - (drawerLayout.width.toFloat() * slideOffset * 0.28f).toInt()
                content.layoutParams = lp

                //animaciones al salir del menu
                navigationView.scaleX = scale
                navigationView.scaleY = scale
                navigationView.alpha = slideOffset + 0.1f

                menuLayout.tooolsContent.scaleX = 2.25f - slideOffset
                menuLayout.tooolsContent.scaleY = 2.25f - slideOffset
                menuLayout.tooolsContent.alpha = slideOffset + 0.1f
            }
        }

        override fun onDrawerOpened(drawer: View) {}

        override fun onDrawerClosed(drawer: View) {}

        override fun onDrawerStateChanged(newState: Int) {}
    }

    fun backImageIERP() {
        binding.apply{
            imgIerp.setOnClickListener {
                contentFragment.findNavController().navigate(R.id.fichajeFragment)
            }
        }
    }

    override fun onBackPressed() {
        if (binding.contentFragment.findNavController().currentDestination?.id == R.id.fichajeFragment) {
            moveTaskToBack(true)
        } else {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }

    //funcion para volver a login
    fun onBackToLogin() {
        val pairs: Array<androidx.core.util.Pair<View, String>> = arrayOf(
            androidx.core.util.Pair(binding.imgIerp as View, resources.getString(R.string.trans_img_ierp))
        )

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity, *pairs)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent, options.toBundle())
    }

    override fun selectedSeccion(seccionMenu: ConstantHelper.SeccionMenu) {
        binding.contentFragment.findNavController().let { navController ->

            var id: Int = R.id.fichajeFragment

            when (seccionMenu) {
                ConstantHelper.SeccionMenu.fichajes -> {
                    id = R.id.fichajeFragment
                }
                ConstantHelper.SeccionMenu.guardias -> {
                    id = R.id.guardiasFragment
                }
                ConstantHelper.SeccionMenu.proyectos -> {
                    id = R.id.proyectosFragment
                }
                ConstantHelper.SeccionMenu.tareas -> {
                    id = R.id.tareasFragment
                }
                ConstantHelper.SeccionMenu.tareasAsignadas -> {
                    id = R.id.tareasAsignadasFragment
                }
                ConstantHelper.SeccionMenu.soportes -> {
                    id = R.id.soportesFragment
                }
                ConstantHelper.SeccionMenu.notificaciones -> {
                }
            }

            binding.drawerLayout.closeDrawer(binding.navigationView)

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

                    adapterMenu?.let {
                        it.setNotificaciones(false)
                        binding.menuLayout.recyclerMenu.adapter = it
                    }
                },
                button2 = resources.getString(R.string.no),
                completion2 = {
                    adapterMenu?.let {
                        it.setNotificaciones(
                            prefs.getBoolean(
                                ConstantHelper.sendNotificacion,
                                true
                            )
                        )
                        binding.menuLayout.recyclerMenu.adapter = it
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

            adapterMenu?.let {
                it.setNotificaciones(true)
                binding.menuLayout.recyclerMenu.adapter = it
            }
        }
    }

    fun logOut() {
        val editor = prefs.edit()
        editor.putBoolean(ConstantHelper.autoLogin, false)
        editor.putString(ConstantHelper.usuarioLogin, null)
        editor.apply()

        onBackToLogin()
    }
}
