package com.foodchain.iotsolution.presentation.device.add

import com.foodchain.iotsolution.domain.model.DeviceControl
import com.foodchain.iotsolution.domain.model.DeviceType

data class AddDeviceUiState(
    val name: String = "",
    val selectedType: DeviceType = DeviceType.CUSTOM,
    val latitude: String = "",
    val longitude: String = "",
    val address: String = "",
    val controls: List<DeviceControl> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
