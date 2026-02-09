package com.foodchain.iotsolution.presentation.device.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.usecase.device.ControlDeviceUseCase
import com.foodchain.iotsolution.domain.usecase.device.GetDeviceByIdUseCase
import com.foodchain.iotsolution.domain.usecase.device.ObserveDeviceUpdatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DeviceDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDeviceByIdUseCase: GetDeviceByIdUseCase,
    private val controlDeviceUseCase: ControlDeviceUseCase,
    private val observeDeviceUpdatesUseCase: ObserveDeviceUpdatesUseCase
) : ViewModel() {

    private val deviceId: String = savedStateHandle.get<String>("deviceId") ?: ""

    private val _uiState = MutableStateFlow(DeviceDetailUiState())
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()

    init {
        loadDevice()
        observeMqttUpdates()
    }

    fun refresh() {
        loadDevice()
    }

    fun sendControl(controlId: String, value: String) {
        _uiState.update { it.copy(controlError = null) }

        controlDeviceUseCase(deviceId, controlId, value).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    // Control sending in progress
                }
                is Resource.Success -> {
                    // Optimistically update the control value in the UI
                    _uiState.update { state ->
                        val updatedDevice = state.device?.let { device ->
                            device.copy(
                                controls = device.controls.map { control ->
                                    if (control.id == controlId) {
                                        control.copy(currentValue = value)
                                    } else {
                                        control
                                    }
                                }
                            )
                        }
                        state.copy(device = updatedDevice, controlError = null)
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(controlError = result.message ?: "Failed to send control command")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadDevice() {
        getDeviceByIdUseCase(deviceId).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            device = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load device"
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun observeMqttUpdates() {
        if (deviceId.isBlank()) return

        observeDeviceUpdatesUseCase(deviceId).onEach { mqttMessage ->
            // Update control values when MQTT message arrives for this device
            _uiState.update { state ->
                val device = state.device ?: return@update state
                // Parse MQTT payload - expected format: "controlId:value"
                val parts = mqttMessage.payload.split(":", limit = 2)
                if (parts.size == 2) {
                    val controlId = parts[0]
                    val newValue = parts[1]
                    val updatedControls = device.controls.map { control ->
                        if (control.id == controlId) {
                            control.copy(currentValue = newValue)
                        } else {
                            control
                        }
                    }
                    state.copy(device = device.copy(controls = updatedControls))
                } else {
                    state
                }
            }
        }.launchIn(viewModelScope)
    }
}
