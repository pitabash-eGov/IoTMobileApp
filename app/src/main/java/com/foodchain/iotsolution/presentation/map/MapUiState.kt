package com.foodchain.iotsolution.presentation.map

import com.foodchain.iotsolution.domain.model.Device

data class MapUiState(
    val devices: List<Device> = emptyList(),
    val selectedDevice: Device? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
