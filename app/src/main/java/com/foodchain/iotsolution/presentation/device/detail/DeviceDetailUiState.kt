package com.foodchain.iotsolution.presentation.device.detail

import com.foodchain.iotsolution.domain.model.Device

data class DeviceDetailUiState(
    val device: Device? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val controlError: String? = null
)
