package com.toools.ierp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.toools.ierp.alarm.Alarms
import com.toools.ierp.helpers.ConstantsHelper
import com.toools.ierp.helpers.prefs
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import java.util.*


@HiltAndroidApp
//TODO: multidexApplication o aplication?
class IerpApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        mInstance = this

        val options = FirebaseOptions.Builder()
            .setProjectId(ConstantsHelper.firebaseProjectID)
            .setApplicationId(ConstantsHelper.firebaseApplicationID)
            .setApiKey(ConstantsHelper.firebaseApiKey)
            .build()

        FirebaseApp.initializeApp(this, options, ConstantsHelper.firebaseAppName)

        //Inicializar fuente por defecto
        ViewPump.init(
            ViewPump.builder().addInterceptor(
                CalligraphyInterceptor(
                    CalligraphyConfig.Builder()
                        .setDefaultFontPath(getString(R.string.default_font))
//                        .setFontAttrId(R.attr.fontPath)
                        .build()
                )
            )
                .build()
        )


        if (prefs.getBoolean(ConstantsHelper.sendNotificacion, true)) {

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

            if (!prefs.contains(ConstantsHelper.horarioVerano)) {
                Alarms.getInstance().createAlarmn(this, isVerano)
            } else {
                if (prefs.getBoolean(ConstantsHelper.horarioVerano, true) != isVerano) {
                    Alarms.getInstance().createAlarmn(this, isVerano)
                }
            }

            val editor = prefs.edit()
            editor.putBoolean(ConstantsHelper.sendNotificacion, true)
            editor.putBoolean(ConstantsHelper.horarioVerano, isVerano)
            editor.apply()

        } else {
            Alarms.getInstance().deleteAllAlarms(this)
        }
    }

    //region STATIC ACCESS
    companion object {
        private lateinit var mInstance: Application
        fun getInstance(): Application {
            return mInstance
        }
    }
}