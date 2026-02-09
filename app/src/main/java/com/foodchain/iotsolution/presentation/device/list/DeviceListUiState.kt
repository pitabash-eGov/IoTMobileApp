package com.foodchain.iotsolution.presentation.device.list

import com.foodchain.iotsolution.domain.model.Device
import com.foodchain.iotsolution.domain.model.DeviceType

data class DeviceListUiState(
    val devices: List<Device> = emptyList(),
    val filteredDevices: List<Device> = emptyList(),
    val searchQuery: String = "",
    val selectedType: DeviceType? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
