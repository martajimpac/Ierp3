package com.toools.ierp.core

import android.app.*
import android.content.Intent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.PersistableBundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.toools.ierp.R
import com.toools.ierp.core.Alarms.Companion.CODE_DESC
import com.toools.ierp.core.Alarms.Companion.CODE_ID
import com.toools.ierp.ui.splash.SplashActivity


private const val NOTIFICATION_INFO = 0xFC0001
private const val NOTIFICATION_INFO_REQUEST = 0xFC0002

class DeviceBootRecived : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        var descNotifi = context.getString(R.string.notifi_desc_salir)
        if(intent.getStringExtra(CODE_DESC)!=null){
            descNotifi = intent.getStringExtra(CODE_DESC)!!
            Log.e("intent",descNotifi)

            Log.e("requestId",intent.getIntExtra("requestId", 100).toString())
        }

        mostrarNotificacion(context,descNotifi)

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun scheduleJob(context: Context, descNotifi : String, params: JobParameters?) {

        mostrarNotificacion(context,descNotifi)


        params?.extras?.getInt(CODE_ID)?.let {

            Log.e("job","Crea el job periodico")

            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val listPending = jobScheduler.allPendingJobs

            for(jobInfo in listPending){
                if(jobInfo.id == it && !jobInfo.isPeriodic){
                    jobScheduler.cancel(it)

                    val serviceComponent = ComponentName(context, AlarmJobService::class.java)
                    val builder = JobInfo.Builder(it, serviceComponent)
                    builder.setPeriodic(AlarmManager.INTERVAL_DAY*7)
                    val bundle = PersistableBundle()
                    bundle.putString(CODE_DESC, descNotifi)
                    bundle.putInt(CODE_ID, it)

                    builder.setExtras(bundle)

                    jobScheduler.schedule(builder.build())

                    break
                }
            }

        }
    }

    fun mostrarNotificacion(context: Context, descNotifi: String){

        val resultIntent = Intent(context, SplashActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_INFO_REQUEST, resultIntent, PendingIntent.FLAG_IMMUTABLE)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = "com.toools.rfef.channelId"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(notificationChannelId, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = descNotifi
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, notificationChannelId)

        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_notifi)
            .setContentText(descNotifi)
            .setContentTitle(context.getString(R.string.notifi_titulo))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        notificationManager.notify(NOTIFICATION_INFO, notificationBuilder.build())

        Log.e("Notificacion","Entra en notificacion")
    }
}