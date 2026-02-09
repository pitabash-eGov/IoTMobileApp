package com.foodchain.iotsolution.data.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.foodchain.iotsolution.core.constants.AppConstants
import com.foodchain.iotsolution.data.local.DataStoreManager
import com.foodchain.iotsolution.data.remote.mqtt.ConnectionState
import com.foodchain.iotsolution.data.remote.mqtt.MqttManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MqttForegroundService : LifecycleService() {

    companion object {
        private const val TAG = "MqttForegroundService"

        fun start(context: Context) {
            val intent = Intent(context, MqttForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, MqttForegroundService::class.java))
        }
    }

    @Inject
    lateinit var mqttManager: MqttManager

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val notification = notificationHelper.buildServiceNotification(mqttManager.isConnected)
        startForeground(AppConstants.NOTIFICATION_ID_SERVICE, notification)

        connectMqttIfNeeded()
        observeConnectionState()
        observeMessages()

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        super.onDestroy()
    }

    private fun connectMqttIfNeeded() {
        if (!mqttManager.isConnected) {
            lifecycleScope.launch {
                val brokerUrl = dataStoreManager.mqttBrokerUrl.first()
                if (!brokerUrl.isNullOrBlank()) {
                    mqttManager.connect(brokerUrl = brokerUrl)
                } else {
                    mqttManager.connect()
                }
            }
        }
    }

    private fun observeConnectionState() {
        lifecycleScope.launch {
            mqttManager.connectionState.collect { state ->
                val isConnected = state == ConnectionState.CONNECTED
                val notification = notificationHelper.buildServiceNotification(isConnected)
                notificationHelper.let {
                    try {
                        val nm = getSystemService(Context.NOTIFICATION_SERVICE)
                                as android.app.NotificationManager
                        nm.notify(AppConstants.NOTIFICATION_ID_SERVICE, notification)
                    } catch (e: SecurityException) {
                        Log.w(TAG, "Cannot update notification", e)
                    }
                }

                // Show connection status notification if user has it enabled
                val connectionAlertsEnabled = dataStoreManager.connectionAlertsEnabled.first()
                if (connectionAlertsEnabled) {
                    when (state) {
                        ConnectionState.CONNECTED -> notificationHelper.showConnectionStatus(true)
                        ConnectionState.DISCONNECTED -> notificationHelper.showConnectionStatus(false)
                        else -> { /* no notification for CONNECTING or ERROR */ }
                    }
                }
            }
        }
    }

    private fun observeMessages() {
        lifecycleScope.launch {
            mqttManager.messages.collect { message ->
                val deviceAlertsEnabled = dataStoreManager.deviceAlertsEnabled.first()
                if (!deviceAlertsEnabled) return@collect

                // Parse device name from topic (e.g., "devices/living-room-sensor/status")
                val topicParts = message.topic.split("/")
                if (topicParts.size >= 3 && topicParts[0] == "devices") {
                    val deviceName = topicParts[1].replace("-", " ").replaceFirstChar { it.uppercase() }
                    val topicType = topicParts[2]

                    when (topicType) {
                        "status" -> {
                            notificationHelper.showDeviceAlert(
                                deviceName = deviceName,
                                message = "Status: ${message.payload}"
                            )
                        }
                        "telemetry" -> {
                            // Only alert on threshold-type payloads containing "alert" or "warning"
                            val payload = message.payload.lowercase()
                            if (payload.contains("alert") || payload.contains("warning")) {
                                notificationHelper.showDeviceAlert(
                                    deviceName = deviceName,
                                    message = message.payload
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
