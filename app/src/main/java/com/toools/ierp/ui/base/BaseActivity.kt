package com.toools.ierp.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import io.github.inflationx.viewpump.ViewPumpContextWrapper

@SuppressLint("Registered")
open class BaseActivity: AppCompatActivity() {

    var mIsRunning = true

    fun byTooolsClickListener(){
        val url = "https://www.toools.es"

        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    override fun attachBaseContext(newBase: Context) {

        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onResume() {

        super.onResume()
        mIsRunning = true
    }

    override fun onPause() {

        super.onPause()
        mIsRunning = false
    }
}