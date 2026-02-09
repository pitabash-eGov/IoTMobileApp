package com.foodchain.iotsolution.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.data.local.DataStoreManager
import com.foodchain.iotsolution.domain.model.Device
import com.foodchain.iotsolution.domain.usecase.device.GetAllDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllDevicesUseCase: GetAllDevicesUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    val lastLocation = MutableStateFlow<Pair<Double, Double>?>(null)

    init {
        loadDevices()
        loadLastLocation()
    }

    fun onDeviceSelected(device: Device?) {
        _uiState.update { it.copy(selectedDevice = device) }
    }

    private fun loadDevices() {
        getAllDevicesUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
                is Resource.Success -> {
                    val devices = result.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            devices = devices,
                            isLoading = false,
                            error = null
                        )
                    }
                    // Save the first device location as last location if available
                    devices.firstOrNull { it.location.latitude != 0.0 || it.location.longitude != 0.0 }?.let { device ->
                        viewModelScope.launch {
                            dataStoreManager.saveLastLocation(
                                device.location.latitude,
                                device.location.longitude
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load devices"
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadLastLocation() {
        viewModelScope.launch {
            val location = dataStoreManager.getLastLocation()
            lastLocation.value = location
        }
    }
}
