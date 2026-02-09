package com.foodchain.iotsolution.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.foodchain.iotsolution.presentation.components.DeviceIcon
import com.foodchain.iotsolution.presentation.components.ErrorMessage
import com.foodchain.iotsolution.presentation.components.IoTTopBar
import com.foodchain.iotsolution.presentation.components.LoadingIndicator
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDeviceDetail: (String) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lastLocation by viewModel.lastLocation.collectAsState()
    val scope = rememberCoroutineScope()

    val defaultLatLng = LatLng(28.6139, 77.2090)
    val initialPosition = lastLocation?.let { LatLng(it.first, it.second) } ?: defaultLatLng

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 12f)
    }

    LaunchedEffect(lastLocation) {
        lastLocation?.let { loc ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(loc.first, loc.second), 12f)
            )
        }
    }

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)

    Scaffold(
        topBar = {
            IoTTopBar(
                title = "Device Map",
                onNavigateBack = onNavigateBack
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
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = if (uiState.selectedDevice != null) 200.dp else 0.dp,
                    sheetContent = {
                        DeviceBottomSheet(
                            device = uiState.selectedDevice,
                            onViewDetails = { deviceId ->
                                onNavigateToDeviceDetail(deviceId)
                            }
                        )
                    },
                    modifier = Modifier.padding(paddingValues)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState
                        ) {
                            uiState.devices.forEach { device ->
                                if (device.location.latitude != 0.0 || device.location.longitude != 0.0) {
                                    Marker(
                                        state = MarkerState(
                                            position = LatLng(
                                                device.location.latitude,
                                                device.location.longitude
                                            )
                                        ),
                                        title = device.name,
                                        snippet = "${device.type.displayName} - ${if (device.isOnline) "Online" else "Offline"}",
                                        onClick = {
                                            viewModel.onDeviceSelected(device)
                                            scope.launch {
                                                bottomSheetState.expand()
                                            }
                                            true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceBottomSheet(
    device: com.foodchain.iotsolution.domain.model.Device?,
    onViewDetails: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (device != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                DeviceIcon(
                    deviceType = device.type,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = device.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = device.type.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = null,
                        tint = if (device.isOnline) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (device.isOnline) "Online" else "Offline",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (device.location.address.isNotBlank()) {
                Text(
                    text = device.location.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = "%.4f, %.4f".format(device.location.latitude, device.location.longitude),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onViewDetails(device.id) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Details")
            }
        } else {
            Text(
                text = "Select a device on the map",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
