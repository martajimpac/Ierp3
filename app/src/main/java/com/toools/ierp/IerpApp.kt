package com.toools.ierp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.toools.ierp.data.ConstantHelper
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump


@HiltAndroidApp
class IerpApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        mInstance = this

        val options = FirebaseOptions.Builder()
            .setProjectId(ConstantHelper.firebaseProjectID)
            .setApplicationId(ConstantHelper.firebaseApplicationID)
            .setApiKey(ConstantHelper.firebaseApiKey)
            .build()

        FirebaseApp.initializeApp(this, options, ConstantHelper.firebaseAppName)

        //Inicializar fuente por defecto
        ViewPump.init(
            ViewPump.builder().addInterceptor(
                CalligraphyInterceptor(
                    CalligraphyConfig.Builder()
                        .setDefaultFontPath(getString(R.string.default_font))
                        .setFontAttrId(androidx.core.R.attr.font)
                        .build()
                )
            )
                .build()
        )
    }

    //region STATIC ACCESS
    companion object {
        private lateinit var mInstance: Application
        fun getInstance(): Application {
            return mInstance
        }
    }
}