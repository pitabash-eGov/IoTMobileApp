package com.foodchain.iotsolution.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.data.local.DataStoreManager
import com.foodchain.iotsolution.data.remote.mqtt.ConnectionState
import com.foodchain.iotsolution.data.remote.mqtt.MqttManager
import com.foodchain.iotsolution.data.service.MqttForegroundService
import com.foodchain.iotsolution.domain.usecase.auth.LogoutUseCase
import com.foodchain.iotsolution.domain.usecase.device.GetAllDevicesUseCase
import com.foodchain.iotsolution.domain.usecase.weather.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getAllDevicesUseCase: GetAllDevicesUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val dataStoreManager: DataStoreManager,
    private val mqttManager: MqttManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    companion object {
        private const val DEFAULT_LATITUDE = 28.6139
        private const val DEFAULT_LONGITUDE = 77.2090
    }

    init {
        loadUserName()
        loadDevices()
        loadWeather()
        observeMqttConnectionState()
        startServiceIfEnabled()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            val name = dataStoreManager.getUserName() ?: "User"
            _uiState.update { it.copy(userName = name) }
        }
    }

    fun refreshDevices() {
        loadDevices()
    }

    fun refreshWeather() {
        loadWeather()
    }

    private fun loadDevices() {
        getAllDevicesUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            devices = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
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

    private fun loadWeather() {
        viewModelScope.launch {
            val location = dataStoreManager.getLastLocation()
            val lat = location?.first ?: DEFAULT_LATITUDE
            val lon = location?.second ?: DEFAULT_LONGITUDE

            getWeatherUseCase(lat, lon).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(weatherData = result.data) }
                    }
                    is Resource.Error -> {
                        // Weather errors are non-critical; don't overwrite main error
                    }
                    is Resource.Loading -> {
                        // No separate loading state for weather
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun observeMqttConnectionState() {
        mqttManager.connectionState.onEach { state ->
            _uiState.update {
                it.copy(mqttConnected = state == ConnectionState.CONNECTED)
            }
        }.launchIn(viewModelScope)
    }

    private fun startServiceIfEnabled() {
        viewModelScope.launch {
            val enabled = dataStoreManager.notificationsEnabled.first()
            if (enabled) {
                MqttForegroundService.start(context)
            }
        }
    }
}
