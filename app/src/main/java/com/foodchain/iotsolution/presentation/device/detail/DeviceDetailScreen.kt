package com.foodchain.iotsolution.presentation.device.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.foodchain.iotsolution.domain.model.ControlType
import com.foodchain.iotsolution.domain.model.DeviceControl
import com.foodchain.iotsolution.presentation.components.ControlButton
import com.foodchain.iotsolution.presentation.components.ControlSlider
import com.foodchain.iotsolution.presentation.components.DeviceIcon
import com.foodchain.iotsolution.presentation.components.ErrorMessage
import com.foodchain.iotsolution.presentation.components.IoTTopBar
import com.foodchain.iotsolution.presentation.components.LoadingIndicator
import com.foodchain.iotsolution.presentation.components.ToggleSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: DeviceDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.controlError) {
        uiState.controlError?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        topBar = {
            IoTTopBar(
                title = uiState.device?.name ?: "Device Details",
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null -> {
                ErrorMessage(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.device != null -> {
                val device = uiState.device!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Device Header
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    DeviceIcon(
                                        deviceType = device.type,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = device.name,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = device.type.displayName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Circle,
                                            contentDescription = null,
                                            tint = if (device.isOnline) Color(0xFF4CAF50) else Color(0xFFF44336),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (device.isOnline) "Online" else "Offline",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (device.isOnline) Color(0xFF4CAF50) else Color(0xFFF44336)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Location Info
                    item {
                        if (device.location.address.isNotBlank() ||
                            device.location.latitude != 0.0 ||
                            device.location.longitude != 0.0
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = "Location",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        if (device.location.label.isNotBlank()) {
                                            Text(
                                                text = device.location.label,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        if (device.location.address.isNotBlank()) {
                                            Text(
                                                text = device.location.address,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = "%.4f, %.4f".format(
                                                device.location.latitude,
                                                device.location.longitude
                                            ),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Controls Section Header
                    item {
                        Text(
                            text = "Controls",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Dynamic Controls
                    if (device.controls.isEmpty()) {
                        item {
                            Text(
                                text = "No controls configured for this device",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(
                            items = device.controls,
                            key = { it.id }
                        ) { control ->
                            DeviceControlItem(
                                control = control,
                                onControlAction = { controlId, value ->
                                    viewModel.sendControl(controlId, value)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceControlItem(
    control: DeviceControl,
    onControlAction: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = control.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            when (control.controlType) {
                ControlType.TOGGLE -> {
                    ToggleSwitch(
                        control = control,
                        onToggle = { isOn ->
                            onControlAction(control.id, if (isOn) "true" else "false")
                        }
                    )
                }
                ControlType.SLIDER -> {
                    ControlSlider(
                        control = control,
                        onValueChange = { value ->
                            onControlAction(control.id, value.toString())
                        }
                    )
                }
                ControlType.BUTTON -> {
                    ControlButton(
                        control = control,
                        onClick = {
                            onControlAction(control.id, "pressed")
                        }
                    )
                }
                ControlType.DROPDOWN -> {
                    var expanded by remember { mutableStateOf(false) }
                    var selectedOption by remember(control.currentValue) {
                        mutableStateOf(control.currentValue)
                    }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedOption,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            control.options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedOption = option
                                        expanded = false
                                        onControlAction(control.id, option)
                                    }
                                )
                            }
                        }
                    }
                }
                ControlType.COLOR_PICKER -> {
                    // Basic color display with hex value
                    Text(
                        text = "Current: ${control.currentValue}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
