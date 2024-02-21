package com.apu.unsession

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.drawable.Icon
import android.util.Log
import api.ApiClient.Users.sendFcmToken
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationService : FirebaseMessagingService() {
    private fun notificator() = NotificationManager::class.java.cast(getSystemService(NOTIFICATION_SERVICE)) as NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificator().createNotificationChannel(NotificationChannel("Default", "Default", NotificationManager.IMPORTANCE_DEFAULT))
    }
    override fun onSendError(msgId: String, exception: Exception) {
        super.onSendError(msgId, exception)
        Log.e(TAG, "Error sending message: $msgId", exception)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        CoroutineScope(Dispatchers.IO).launch {
            sendFcmToken(token)
        }
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message data: ${remoteMessage.data}")
        val notification = createNotification(remoteMessage)
        postNotification(notification)
    }

    private fun postNotification(notification: Notification) {
        notificator().notify(0, notification)
    }

    private fun createNotification(remoteMessage: RemoteMessage, channelId: String = "Default"): Notification {
        return Notification.Builder(this, channelId)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setSmallIcon(Icon.createWithResource(this, R.drawable.placeholder))
            .build()
    }

    companion object {
        private const val TAG = "NotificationService"
    }
}
