package com.foodchain.iotsolution.data.remote.mqtt

import android.content.Context
import android.util.Log
import com.foodchain.iotsolution.BuildConfig
import com.foodchain.iotsolution.core.constants.AppConstants
import com.foodchain.iotsolution.domain.model.MqttMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage as PahoMqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import javax.inject.Inject
import javax.inject.Singleton

enum class ConnectionState {
    CONNECTED, DISCONNECTED, CONNECTING, ERROR
}

@Singleton
class MqttManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "MqttManager"
    }

    private var mqttClient: MqttClient? = null

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _messages = MutableSharedFlow<MqttMessage>(replay = 0, extraBufferCapacity = 64)
    val messages: SharedFlow<MqttMessage> = _messages.asSharedFlow()

    private val subscribedTopics = mutableSetOf<String>()

    fun connect(brokerUrl: String? = null, clientId: String? = null) {
        if (_connectionState.value == ConnectionState.CONNECTED || _connectionState.value == ConnectionState.CONNECTING) {
            return
        }

        _connectionState.value = ConnectionState.CONNECTING

        try {
            val url = brokerUrl ?: BuildConfig.MQTT_BROKER_URL
            val id = clientId ?: "iot_solution_${System.currentTimeMillis()}"

            mqttClient = MqttClient(url, id, MemoryPersistence()).apply {
                setCallback(object : MqttCallbackExtended {
                    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                        Log.d(TAG, "Connected to $serverURI (reconnect: $reconnect)")
                        _connectionState.value = ConnectionState.CONNECTED
                        if (reconnect) {
                            resubscribeAll()
                        }
                    }

                    override fun connectionLost(cause: Throwable?) {
                        Log.w(TAG, "Connection lost", cause)
                        _connectionState.value = ConnectionState.DISCONNECTED
                    }

                    override fun messageArrived(topic: String?, message: PahoMqttMessage?) {
                        if (topic != null && message != null) {
                            val mqttMessage = MqttMessage(
                                topic = topic,
                                payload = String(message.payload),
                                qos = message.qos,
                                retained = message.isRetained,
                                timestamp = System.currentTimeMillis()
                            )
                            _messages.tryEmit(mqttMessage)
                        }
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        Log.d(TAG, "Delivery complete")
                    }
                })

                val options = MqttConnectOptions().apply {
                    isCleanSession = true
                    isAutomaticReconnect = true
                    keepAliveInterval = AppConstants.MQTT_KEEP_ALIVE_INTERVAL
                    connectionTimeout = AppConstants.MQTT_CONNECTION_TIMEOUT
                }

                connect(options)
                _connectionState.value = ConnectionState.CONNECTED
            }
        } catch (e: MqttException) {
            Log.e(TAG, "Connection failed", e)
            _connectionState.value = ConnectionState.ERROR
        }
    }

    fun disconnect() {
        try {
            subscribedTopics.clear()
            mqttClient?.disconnect()
            mqttClient?.close()
            mqttClient = null
            _connectionState.value = ConnectionState.DISCONNECTED
        } catch (e: MqttException) {
            Log.e(TAG, "Disconnect failed", e)
        }
    }

    fun subscribe(topic: String, qos: Int = AppConstants.MQTT_DEFAULT_QOS) {
        try {
            mqttClient?.subscribe(topic, qos)
            subscribedTopics.add(topic)
            Log.d(TAG, "Subscribed to $topic")
        } catch (e: MqttException) {
            Log.e(TAG, "Subscribe failed for $topic", e)
        }
    }

    fun unsubscribe(topic: String) {
        try {
            mqttClient?.unsubscribe(topic)
            subscribedTopics.remove(topic)
            Log.d(TAG, "Unsubscribed from $topic")
        } catch (e: MqttException) {
            Log.e(TAG, "Unsubscribe failed for $topic", e)
        }
    }

    fun publish(
        topic: String,
        payload: String,
        qos: Int = AppConstants.MQTT_DEFAULT_QOS,
        retained: Boolean = false
    ) {
        try {
            val message = PahoMqttMessage(payload.toByteArray()).apply {
                this.qos = qos
                this.isRetained = retained
            }
            mqttClient?.publish(topic, message)
            Log.d(TAG, "Published to $topic: $payload")
        } catch (e: MqttException) {
            Log.e(TAG, "Publish failed for $topic", e)
        }
    }

    private fun resubscribeAll() {
        subscribedTopics.forEach { topic ->
            try {
                mqttClient?.subscribe(topic, AppConstants.MQTT_DEFAULT_QOS)
            } catch (e: MqttException) {
                Log.e(TAG, "Resubscribe failed for $topic", e)
            }
        }
    }

    val isConnected: Boolean
        get() = mqttClient?.isConnected == true
}
