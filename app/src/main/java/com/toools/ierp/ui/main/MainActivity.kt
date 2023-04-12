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
import com.google.gson.Gson
import com.toools.ierp.BuildConfig
import com.toools.ierp.R
import com.toools.ierp.core.DialogHelper
import com.toools.ierp.core.prefs
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.LoginResponse
import com.toools.ierp.databinding.ActivityMainBinding
import com.toools.ierp.databinding.MenuLayoutBinding
import com.toools.ierp.ui.base.BaseActivity
import com.toools.ierp.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import java.util.*

const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : BaseActivity(), SeccionesListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var menuBinding: MenuLayoutBinding
    private var adapterSecciones: AdapterSecciones? = null

    var toggle: ActionBarDrawerToggle? = null
    var showingBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpView()
    }

    fun setUpView() {

        val drawerLayout = binding.drawerLayout
        val menuView = binding.menuLayout

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )

        binding.back.setOnClickListener {
            if (drawerLayout.isDrawerOpen(menuView)) {
                drawerLayout.closeDrawer(menuView)
            } else {
                drawerLayout.openDrawer(menuView)
            }
        }

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    override fun selectedSeccion(seccion: ConstantHelper.Seccion) {
        TODO("Not yet implemented")
    }

    override fun changeNotificaciones(isNotificacion: Boolean) {
        TODO("Not yet implemented")
    }
}
