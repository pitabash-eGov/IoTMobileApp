package com.foodchain.iotsolution.domain.model

data class DeviceControl(
    val id: String = "",
    val name: String = "",
    val controlType: ControlType = ControlType.TOGGLE,
    val currentValue: String = "",
    val minValue: Float = 0f,
    val maxValue: Float = 100f,
    val step: Float = 1f,
    val options: List<String> = emptyList(),
    val mqttTopic: String = ""
)
