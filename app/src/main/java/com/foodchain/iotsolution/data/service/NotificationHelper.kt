package com.foodchain.iotsolution.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.foodchain.iotsolution.MainActivity
import com.foodchain.iotsolution.R
import com.foodchain.iotsolution.core.constants.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                AppConstants.CHANNEL_MQTT_SERVICE,
                "MQTT Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Persistent notification for MQTT background connection"
            }

            val deviceAlertsChannel = NotificationChannel(
                AppConstants.CHANNEL_DEVICE_ALERTS,
                "Device Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for device status changes and threshold alerts"
            }

            val connectionChannel = NotificationChannel(
                AppConstants.CHANNEL_CONNECTION_STATUS,
                "Connection Status",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for MQTT connection and disconnection events"
            }

            notificationManager.createNotificationChannels(
                listOf(serviceChannel, deviceAlertsChannel, connectionChannel)
            )
        }
    }

    fun buildServiceNotification(isConnected: Boolean): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val statusText = if (isConnected) "MQTT Connected" else "MQTT Disconnected"

        return NotificationCompat.Builder(context, AppConstants.CHANNEL_MQTT_SERVICE)
            .setContentTitle("IoT Solution")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    fun showDeviceAlert(deviceName: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId =
            AppConstants.NOTIFICATION_ID_DEVICE_ALERT_BASE + (deviceName.hashCode() and 0x7FFFFFFF) % 1000

        val notification = NotificationCompat.Builder(context, AppConstants.CHANNEL_DEVICE_ALERTS)
            .setContentTitle(deviceName)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun showConnectionStatus(connected: Boolean) {
        val title = if (connected) "MQTT Connected" else "MQTT Disconnected"
        val text = if (connected) {
            "Successfully connected to MQTT broker"
        } else {
            "Lost connection to MQTT broker"
        }

        val notification =
            NotificationCompat.Builder(context, AppConstants.CHANNEL_CONNECTION_STATUS)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .build()

        notificationManager.notify(AppConstants.NOTIFICATION_ID_CONNECTION, notification)
    }
}
