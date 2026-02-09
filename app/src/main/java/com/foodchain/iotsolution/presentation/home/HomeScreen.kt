package com.foodchain.iotsolution.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.foodchain.iotsolution.presentation.components.BottomNavBar
import com.foodchain.iotsolution.presentation.components.DeviceCard
import com.foodchain.iotsolution.presentation.components.ErrorMessage
import com.foodchain.iotsolution.presentation.components.IoTTopBar
import com.foodchain.iotsolution.presentation.components.LoadingIndicator
import com.foodchain.iotsolution.presentation.components.MapPreview
import com.foodchain.iotsolution.presentation.components.WeatherWidget
import com.foodchain.iotsolution.presentation.navigation.Screen

@Composable
fun HomeScreen(
    onNavigateToDeviceList: () -> Unit,
    onNavigateToDeviceDetail: (String) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            IoTTopBar(
                title = "IoT Dashboard",
                actions = {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = if (uiState.mqttConnected) "MQTT Connected" else "MQTT Disconnected",
                        tint = if (uiState.mqttConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.Home.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.Home.route -> { /* Already on Home */ }
                        Screen.DeviceList.route -> onNavigateToDeviceList()
                        Screen.Map.route -> onNavigateToMap()
                        Screen.Settings.route -> onNavigateToSettings()
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.devices.isEmpty() -> {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null && uiState.devices.isEmpty() -> {
                ErrorMessage(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.refreshDevices() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                HomeContent(
                    uiState = uiState,
                    onNavigateToDeviceList = onNavigateToDeviceList,
                    onNavigateToDeviceDetail = onNavigateToDeviceDetail,
                    onNavigateToMap = onNavigateToMap,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onNavigateToDeviceList: () -> Unit,
    onNavigateToDeviceDetail: (String) -> Unit,
    onNavigateToMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Greeting
        item {
            Text(
                text = "Hello, ${uiState.userName}!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Weather Widget
        item {
            uiState.weatherData?.let { weather ->
                WeatherWidget(
                    weatherData = weather,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // My Devices Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Devices",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onNavigateToDeviceList) {
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Horizontal Device Cards (max 5)
        item {
            if (uiState.devices.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No devices added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.devices.take(5),
                        key = { it.id }
                    ) { device ->
                        DeviceCard(
                            device = device,
                            onClick = { onNavigateToDeviceDetail(device.id) },
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            }
        }

        // Map Preview
        item {
            Spacer(modifier = Modifier.height(8.dp))
            MapPreview(
                deviceCount = uiState.devices.count { it.location.latitude != 0.0 || it.location.longitude != 0.0 },
                onClick = onNavigateToMap,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
