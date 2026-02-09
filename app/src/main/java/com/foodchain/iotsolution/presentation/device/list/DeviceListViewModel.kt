package com.foodchain.iotsolution.presentation.device.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.model.Device
import com.foodchain.iotsolution.domain.model.DeviceType
import com.foodchain.iotsolution.domain.usecase.device.DeleteDeviceUseCase
import com.foodchain.iotsolution.domain.usecase.device.GetAllDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DeviceListViewModel @Inject constructor(
    private val getAllDevicesUseCase: GetAllDevicesUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceListUiState())
    val uiState: StateFlow<DeviceListUiState> = _uiState.asStateFlow()

    init {
        loadDevices()
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
                    applyFilters()
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

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onFilterByType(type: DeviceType?) {
        _uiState.update {
            it.copy(selectedType = if (it.selectedType == type) null else type)
        }
        applyFilters()
    }

    fun deleteDevice(id: String) {
        deleteDeviceUseCase(id).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    // Optionally show a loading state
                }
                is Resource.Success -> {
                    _uiState.update { state ->
                        val updatedDevices = state.devices.filter { it.id != id }
                        state.copy(devices = updatedDevices)
                    }
                    applyFilters()
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(error = result.message ?: "Failed to delete device")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun applyFilters() {
        _uiState.update { state ->
            val filtered = state.devices.filter { device ->
                val matchesSearch = state.searchQuery.isBlank() ||
                    device.name.contains(state.searchQuery, ignoreCase = true) ||
                    device.type.displayName.contains(state.searchQuery, ignoreCase = true)
                val matchesType = state.selectedType == null || device.type == state.selectedType
                matchesSearch && matchesType
            }
            state.copy(filteredDevices = filtered)
        }
    }
}
