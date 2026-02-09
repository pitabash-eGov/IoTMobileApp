package com.foodchain.iotsolution.presentation.device.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.model.ControlType
import com.foodchain.iotsolution.domain.model.DeviceControl
import com.foodchain.iotsolution.domain.model.DeviceLocation
import com.foodchain.iotsolution.domain.model.DeviceType
import com.foodchain.iotsolution.domain.usecase.device.AddDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val addDeviceUseCase: AddDeviceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDeviceUiState())
    val uiState: StateFlow<AddDeviceUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    fun onTypeChange(type: DeviceType) {
        _uiState.update { it.copy(selectedType = type, error = null) }
    }

    fun onLocationChange(lat: String, lon: String, address: String) {
        _uiState.update {
            it.copy(latitude = lat, longitude = lon, address = address, error = null)
        }
    }

    fun addControl(name: String, type: ControlType) {
        if (name.isBlank()) return
        val control = DeviceControl(
            id = UUID.randomUUID().toString(),
            name = name,
            controlType = type,
            currentValue = when (type) {
                ControlType.TOGGLE -> "false"
                ControlType.SLIDER -> "0"
                ControlType.BUTTON -> ""
                ControlType.DROPDOWN -> ""
                ControlType.COLOR_PICKER -> "#FFFFFF"
            }
        )
        _uiState.update { it.copy(controls = it.controls + control, error = null) }
    }

    fun removeControl(index: Int) {
        _uiState.update { state ->
            val updatedControls = state.controls.toMutableList().apply {
                if (index in indices) removeAt(index)
            }
            state.copy(controls = updatedControls)
        }
    }

    fun onSubmit() {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Device name is required") }
            return
        }

        val lat = state.latitude.toDoubleOrNull() ?: 0.0
        val lon = state.longitude.toDoubleOrNull() ?: 0.0
        val location = DeviceLocation(
            latitude = lat,
            longitude = lon,
            address = state.address
        )

        addDeviceUseCase(
            name = state.name.trim(),
            type = state.selectedType,
            location = location,
            controls = state.controls
        ).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isSuccess = true, error = null)
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to add device"
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}
