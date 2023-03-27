package com.toools.ierp.core

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Handler
import android.util.Log


class AlarmJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {

        Log.d(this.javaClass.simpleName, "onStartJob")
        val mHandler = Handler(mainLooper)
        mHandler.post {
            jobFinished(params, false)
        }

        val desNotifi = params?.extras?.getString(Alarms.CODE_DESC) ?: ""
        DeviceBootRecived().scheduleJob(applicationContext, desNotifi, params)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}