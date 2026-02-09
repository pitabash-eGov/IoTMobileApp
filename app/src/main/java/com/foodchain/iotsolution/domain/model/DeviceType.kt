package com.foodchain.iotsolution.domain.model

enum class DeviceType(val displayName: String, val icon: String) {
    LIGHT("Light", "lightbulb"),
    THERMOSTAT("Thermostat", "thermostat"),
    SWITCH("Switch", "toggle_on"),
    SENSOR("Sensor", "sensors"),
    CAMERA("Camera", "videocam"),
    LOCK("Lock", "lock"),
    FAN("Fan", "air"),
    CUSTOM("Custom", "devices_other")
}
