package com.toools.ierp.data.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.toools.ierp.R
import com.toools.ierp.ui.main.MainActivity


class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        sendNotification(message.notification!!.body, message.data)
    }

    /**
     * Metodo empleado para mostrar la notificacion asociada al servicio push
     */
    private fun sendNotification(messageBody: String?, params: Map<String, String>?) {
        var url: String? = null
        if (params != null && params.containsKey("url")) { //NON-NLS
            url = params["url"] //NON-NLS
        }
        var node: String? = null
        if (params != null && params.containsKey("node")) { //NON-NLS
            node = params["node"] //NON-NLS
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = "com.toools.ierp.channelId"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                notificationChannelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description =
                getString(R.string.app_notification_channel_description)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val mNotificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java)
        if (url != null) intent.putExtra("url", url) //NON-NLS
        if (node != null) intent.putExtra("node", node) //NON-NLS
        val contentIntent =
            PendingIntent.getActivity(this, 0, intent, FLAG_IMMUTABLE)
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this,notificationChannelId)
            .setSmallIcon(com.toools.ierp.R.drawable.ic_notifi)
            .setContentTitle(resources.getString(com.toools.ierp.R.string.app_name))
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody)) //NON-NLS
            .setAutoCancel(true)
            .setContentText(messageBody) //NON-NLS
        mBuilder.setContentIntent(contentIntent)
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build())
    }

    companion object {
        const val NOTIFICATION_ID = 1
        private const val message = "message" //NON-NLS
    }
}