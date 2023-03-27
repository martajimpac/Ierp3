package com.toools.ierp.core

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import androidx.annotation.RequiresApi
import com.toools.ierp.R
import java.util.*


class Alarms {

    companion object {

        private var mInstance = Alarms()

        //identificadores de las alarmar
        var alarmLunes1 = 0
        var alarmLunes2 = 1
        var alarmLunes3 = 2
        var alarmLunes4 = 3
        var alarmMartes1 = 4
        var alarmMartes2 = 5
        var alarmMartes3 = 6
        var alarmMartes4 = 7
        var alarmMiercoles1 = 8
        var alarmMiercoles2 = 9
        var alarmMiercoles3 = 10
        var alarmMiercoles4 = 11
        var alarmJueves1 = 12
        var alarmJueves2 = 13
        var alarmJueves3 = 14
        var alarmJueves4 = 15
        var alarmViernes1 = 16
        var alarmViernes2 = 17

        //identificadores de las alarmar
        var CODE_DESC = "desc"
        var CODE_ID = "requestId"
        
        val TYPE_8_30 = 0
        val TYPE_14 = 1
        val TYPE_16 = 2
        val TYPE_19 = 3
        val TYPE_7_30 = 4
        val TYPE_15_30 = 5

        val TYPE_8 = 6
        
        val DAY_LUNES = 2
        val DAY_MARTES = 3
        val DAY_MIERCOLES = 4
        val DAY_JUEVES = 5
        val DAY_VIERNES = 6
        

        @Synchronized
        fun getInstance(): Alarms {
            return mInstance
        }
    }

    fun deleteAllAlarms(context: Context){

            removeAllAlarmaJob(context)
        
            /*removeAlarmaJob(context, alarmLunes1)
            removeAlarmaJob(context, alarmLunes2)
            removeAlarmaJob(context, alarmLunes3)
            removeAlarmaJob(context, alarmLunes4)

            removeAlarmaJob(context, alarmMartes1)
            removeAlarmaJob(context, alarmMartes2)
            removeAlarmaJob(context, alarmMartes3)
            removeAlarmaJob(context, alarmMartes4)

            removeAlarmaJob(context, alarmMiercoles1)
            removeAlarmaJob(context, alarmMiercoles2)
            removeAlarmaJob(context, alarmMiercoles3)
            removeAlarmaJob(context, alarmMiercoles4)

            removeAlarmaJob(context, alarmJueves1)
            removeAlarmaJob(context, alarmJueves2)
            removeAlarmaJob(context, alarmJueves3)
            removeAlarmaJob(context, alarmJueves4)

            removeAlarmaJob(context, alarmViernes1)
            removeAlarmaJob(context, alarmViernes2)*/

    }


    fun createAlarmn(context: Context, isSummer: Boolean){

        deleteAllAlarms(context)

        if(!isSummer){

            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmLunes1, DAY_LUNES, TYPE_8_30)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmLunes2, DAY_LUNES, TYPE_14)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmLunes3, DAY_LUNES, TYPE_16)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmLunes4, DAY_LUNES, TYPE_19)

            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmMartes1, DAY_MARTES, TYPE_8_30)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmMartes2, DAY_MARTES, TYPE_14)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmMartes3, DAY_MARTES, TYPE_16)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmMartes4, DAY_MARTES, TYPE_19)

            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmMiercoles1, DAY_MIERCOLES, TYPE_8_30)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmMiercoles2, DAY_MIERCOLES, TYPE_14)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmMiercoles3, DAY_MIERCOLES, TYPE_16)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmMiercoles4, DAY_MIERCOLES, TYPE_19)

            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmJueves1, DAY_JUEVES, TYPE_8_30)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmJueves2, DAY_JUEVES, TYPE_14)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmJueves3, DAY_JUEVES, TYPE_16)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmJueves4, DAY_JUEVES, TYPE_19)

            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmViernes1, DAY_VIERNES, TYPE_8)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmViernes2, DAY_VIERNES, TYPE_14)

            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmViernes2, DAY_MIERCOLES, TYPE_14)

        }else{

            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmLunes1, DAY_LUNES, TYPE_7_30)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmLunes2, DAY_LUNES, TYPE_15_30)

            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmMartes1, DAY_MARTES, TYPE_7_30)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmMartes2, DAY_MARTES, TYPE_15_30)

            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmMiercoles1, DAY_MIERCOLES, TYPE_7_30)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmMiercoles2, DAY_MIERCOLES, TYPE_15_30)

            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmJueves1, DAY_JUEVES, TYPE_7_30)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmJueves2, DAY_JUEVES, TYPE_15_30)

            setAlarmaJob(context, context.getString(R.string.notifi_desc_entrar), alarmViernes1, DAY_VIERNES, TYPE_7_30)
            setAlarmaJob(context, context.getString(R.string.notifi_desc_salir), alarmViernes2, DAY_VIERNES, TYPE_15_30)

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun setAlarmaJob(context: Context, desc: String, requestCode: Int, dia: Int, type: Int){



        val cal = Calendar.getInstance()

        val now = Calendar.getInstance()

        //Log.e("setAlarman", "$dia -- $type")

        cal.set(Calendar.DAY_OF_WEEK, dia)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        when(type){
            //invierno diario
            TYPE_8_30 ->{
                cal.set(Calendar.HOUR_OF_DAY, 8)
                cal.set(Calendar.MINUTE, 25)
            }
            TYPE_14 ->{
                cal.set(Calendar.HOUR_OF_DAY, 13)
                cal.set(Calendar.MINUTE, 55)
            }
            TYPE_16 ->{
                cal.set(Calendar.HOUR_OF_DAY, 15)
                cal.set(Calendar.MINUTE, 55)
            }
            TYPE_19 ->{
                cal.set(Calendar.HOUR_OF_DAY, 18)
                cal.set(Calendar.MINUTE, 55)
            }
            //entrada viernes inviernos
            TYPE_8 ->{
                cal.set(Calendar.HOUR_OF_DAY, 7)
                cal.set(Calendar.MINUTE, 55)
            }

            //horario de verano
            TYPE_7_30 ->{
                cal.set(Calendar.HOUR_OF_DAY, 7)
                cal.set(Calendar.MINUTE, 25)
            }
            TYPE_15_30 ->{
                cal.set(Calendar.HOUR_OF_DAY, 15)
                cal.set(Calendar.MINUTE, 25)
            }
        }

        if(now.after(cal))
            cal.add(Calendar.DATE,7)

        val tiempo = cal.timeInMillis - now.timeInMillis

        val serviceComponent = ComponentName(context, AlarmJobService::class.java)
        val builder = JobInfo.Builder(requestCode, serviceComponent)
        builder.setMinimumLatency(tiempo)
        builder.setOverrideDeadline(tiempo)
        val bundle = PersistableBundle()
        bundle.putString(CODE_DESC, desc)
        bundle.putInt(CODE_ID, requestCode)

        builder.setExtras(bundle)

        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun removeAlarmaJob(context: Context, requestCode: Int){

        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(requestCode)

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun removeAllAlarmaJob(context: Context){

        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancelAll()

    }

}