package com.foodchain.iotsolution.domain.model

data class DeviceLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val label: String = ""
)
