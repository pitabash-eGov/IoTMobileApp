package com.foodchain.iotsolution.domain.model

data class Device(
    val id: String = "",
    val name: String = "",
    val type: DeviceType = DeviceType.CUSTOM,
    val isOnline: Boolean = false,
    val location: DeviceLocation = DeviceLocation(),
    val controls: List<DeviceControl> = emptyList(),
    val mqttTopicPrefix: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
