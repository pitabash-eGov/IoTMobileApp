package com.foodchain.iotsolution.presentation.device.add

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.foodchain.iotsolution.domain.model.ControlType
import com.foodchain.iotsolution.domain.model.DeviceType
import com.foodchain.iotsolution.presentation.components.IoTTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddDeviceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            IoTTopBar(
                title = "Add Device",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Device Name
            item {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = { Text("Device Name") },
                    placeholder = { Text("Enter device name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    isError = uiState.error != null && uiState.name.isBlank()
                )
            }

            // Device Type Dropdown
            item {
                var typeExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = uiState.selectedType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Device Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        DeviceType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    viewModel.onTypeChange(type)
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Location Fields
            item {
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.latitude,
                        onValueChange = {
                            viewModel.onLocationChange(it, uiState.longitude, uiState.address)
                        },
                        label = { Text("Latitude") },
                        placeholder = { Text("28.6139") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )
                    OutlinedTextField(
                        value = uiState.longitude,
                        onValueChange = {
                            viewModel.onLocationChange(uiState.latitude, it, uiState.address)
                        },
                        label = { Text("Longitude") },
                        placeholder = { Text("77.2090") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = uiState.address,
                    onValueChange = {
                        viewModel.onLocationChange(uiState.latitude, uiState.longitude, it)
                    },
                    label = { Text("Address") },
                    placeholder = { Text("Enter address (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }

            // Controls Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Controls",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Add Control Form
            item {
                AddControlForm(
                    onAddControl = { name, type -> viewModel.addControl(name, type) }
                )
            }

            // Control List
            itemsIndexed(
                items = uiState.controls,
                key = { _, control -> control.id }
            ) { index, control ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = control.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = control.controlType.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.removeControl(index) }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Remove control",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Error Message
            if (uiState.error != null) {
                item {
                    Text(
                        text = uiState.error ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Submit Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.onSubmit() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !uiState.isLoading,
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(4.dp)
                        )
                    } else {
                        Text(
                            text = "Add Device",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddControlForm(
    onAddControl: (String, ControlType) -> Unit
) {
    var controlName by remember { mutableStateOf("") }
    var selectedControlType by remember { mutableStateOf(ControlType.TOGGLE) }
    var controlTypeExpanded by remember { mutableStateOf(false) }

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
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = controlName,
                onValueChange = { controlName = it },
                label = { Text("Control Name") },
                placeholder = { Text("e.g., Brightness") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            ExposedDropdownMenuBox(
                expanded = controlTypeExpanded,
                onExpandedChange = { controlTypeExpanded = !controlTypeExpanded }
            ) {
                OutlinedTextField(
                    value = selectedControlType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Control Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = controlTypeExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                ExposedDropdownMenu(
                    expanded = controlTypeExpanded,
                    onDismissRequest = { controlTypeExpanded = false }
                ) {
                    ControlType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                selectedControlType = type
                                controlTypeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    if (controlName.isNotBlank()) {
                        onAddControl(controlName.trim(), selectedControlType)
                        controlName = ""
                        selectedControlType = ControlType.TOGGLE
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Control")
            }
        }
    }
}
