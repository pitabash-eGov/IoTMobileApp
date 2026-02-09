package com.foodchain.iotsolution.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodchain.iotsolution.data.local.DataStoreManager
import com.foodchain.iotsolution.data.remote.mqtt.ConnectionState
import com.foodchain.iotsolution.data.remote.mqtt.MqttManager
import com.foodchain.iotsolution.data.service.MqttForegroundService
import com.foodchain.iotsolution.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStoreManager: DataStoreManager,
    private val logoutUseCase: LogoutUseCase,
    private val mqttManager: MqttManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent: StateFlow<Boolean> = _logoutEvent.asStateFlow()

    init {
        loadUserData()
        observeTheme()
        observeMqttBrokerUrl()
        observeMqttState()
        observeNotificationPreferences()
    }

    fun toggleDarkTheme() {
        viewModelScope.launch {
            val newValue = !_uiState.value.isDarkTheme
            dataStoreManager.setDarkTheme(newValue)
        }
    }

    fun updateMqttBrokerUrl(url: String) {
        _uiState.update { it.copy(mqttBrokerUrl = url) }
        viewModelScope.launch {
            dataStoreManager.saveMqttBrokerUrl(url)
        }
    }

    fun reconnectMqtt() {
        val url = _uiState.value.mqttBrokerUrl
        mqttManager.disconnect()
        if (url.isNotBlank()) {
            mqttManager.connect(brokerUrl = url)
        } else {
            mqttManager.connect()
        }
    }

    fun toggleNotifications() {
        viewModelScope.launch {
            val newValue = !_uiState.value.notificationsEnabled
            dataStoreManager.setNotificationsEnabled(newValue)
            if (newValue) {
                MqttForegroundService.start(context)
            } else {
                MqttForegroundService.stop(context)
            }
        }
    }

    fun toggleDeviceAlerts() {
        viewModelScope.launch {
            val newValue = !_uiState.value.deviceAlertsEnabled
            dataStoreManager.setDeviceAlertsEnabled(newValue)
        }
    }

    fun toggleConnectionAlerts() {
        viewModelScope.launch {
            val newValue = !_uiState.value.connectionAlertsEnabled
            dataStoreManager.setConnectionAlertsEnabled(newValue)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            MqttForegroundService.stop(context)
            logoutUseCase()
            dataStoreManager.clearSession()
            mqttManager.disconnect()
            _uiState.update { it.copy(isLoading = false) }
            _logoutEvent.value = true
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val name = dataStoreManager.getUserName() ?: ""
            val email = dataStoreManager.getUserEmail() ?: ""
            _uiState.update { it.copy(userName = name, userEmail = email) }
        }
    }

    private fun observeTheme() {
        dataStoreManager.isDarkTheme.onEach { isDark ->
            _uiState.update { it.copy(isDarkTheme = isDark) }
        }.launchIn(viewModelScope)
    }

    private fun observeMqttBrokerUrl() {
        dataStoreManager.mqttBrokerUrl.onEach { url ->
            _uiState.update { it.copy(mqttBrokerUrl = url ?: "") }
        }.launchIn(viewModelScope)
    }

    private fun observeMqttState() {
        mqttManager.connectionState.onEach { state ->
            _uiState.update {
                it.copy(mqttConnected = state == ConnectionState.CONNECTED)
            }
        }.launchIn(viewModelScope)
    }

    private fun observeNotificationPreferences() {
        dataStoreManager.notificationsEnabled.onEach { enabled ->
            _uiState.update { it.copy(notificationsEnabled = enabled) }
        }.launchIn(viewModelScope)

        dataStoreManager.deviceAlertsEnabled.onEach { enabled ->
            _uiState.update { it.copy(deviceAlertsEnabled = enabled) }
        }.launchIn(viewModelScope)

        dataStoreManager.connectionAlertsEnabled.onEach { enabled ->
            _uiState.update { it.copy(connectionAlertsEnabled = enabled) }
        }.launchIn(viewModelScope)
    }
}
