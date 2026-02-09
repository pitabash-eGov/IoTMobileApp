package com.foodchain.iotsolution.core.constants

object AppConstants {
    const val MQTT_DEFAULT_QOS = 1
    const val MQTT_KEEP_ALIVE_INTERVAL = 60
    const val MQTT_CONNECTION_TIMEOUT = 30
    const val MQTT_RECONNECT_DELAY = 5000L

    const val WEATHER_ICON_URL = "https://openweathermap.org/img/wn/%s@2x.png"
    const val WEATHER_BASE_URL = "https://api.openweathermap.org/"

    const val DATASTORE_NAME = "iot_solution_prefs"

    const val TOPIC_DEVICE_STATUS = "devices/%s/status"
    const val TOPIC_DEVICE_CONTROL = "devices/%s/control"
    const val TOPIC_DEVICE_TELEMETRY = "devices/%s/telemetry"

    const val NETWORK_TIMEOUT = 30L
    const val TOKEN_PREFIX = "Bearer "

    // Notification Channels
    const val CHANNEL_MQTT_SERVICE = "mqtt_service"
    const val CHANNEL_DEVICE_ALERTS = "device_alerts"
    const val CHANNEL_CONNECTION_STATUS = "connection_status"

    // Notification IDs
    const val NOTIFICATION_ID_SERVICE = 1001
    const val NOTIFICATION_ID_CONNECTION = 1002
    const val NOTIFICATION_ID_DEVICE_ALERT_BASE = 2000
}
