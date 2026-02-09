package com.foodchain.iotsolution.presentation.settings

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = "",
    val isDarkTheme: Boolean = false,
    val mqttBrokerUrl: String = "",
    val mqttConnected: Boolean = false,
    val isLoading: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val deviceAlertsEnabled: Boolean = true,
    val connectionAlertsEnabled: Boolean = true
)
