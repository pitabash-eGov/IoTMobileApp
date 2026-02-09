package com.foodchain.iotsolution.presentation.home

import com.foodchain.iotsolution.domain.model.Device
import com.foodchain.iotsolution.domain.model.WeatherData

data class HomeUiState(
    val devices: List<Device> = emptyList(),
    val weatherData: WeatherData? = null,
    val userName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val mqttConnected: Boolean = false
)
