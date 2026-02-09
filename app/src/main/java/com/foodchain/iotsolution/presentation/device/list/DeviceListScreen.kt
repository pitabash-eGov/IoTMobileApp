package com.foodchain.iotsolution.presentation.device.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.foodchain.iotsolution.domain.model.DeviceType
import com.foodchain.iotsolution.presentation.components.DeviceCard
import com.foodchain.iotsolution.presentation.components.ErrorMessage
import com.foodchain.iotsolution.presentation.components.IoTTopBar
import com.foodchain.iotsolution.presentation.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListScreen(
    onNavigateToDeviceDetail: (String) -> Unit,
    onNavigateToAddDevice: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: DeviceListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            IoTTopBar(
                title = "My Devices",
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddDevice,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Device"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Field
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search devices...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(DeviceType.entries.toList()) { type ->
                    FilterChip(
                        selected = uiState.selectedType == type,
                        onClick = { viewModel.onFilterByType(type) },
                        label = { Text(type.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Content
            when {
                uiState.isLoading && uiState.devices.isEmpty() -> {
                    LoadingIndicator()
                }
                uiState.error != null && uiState.devices.isEmpty() -> {
                    ErrorMessage(
                        message = uiState.error ?: "Unknown error",
                        onRetry = { viewModel.onSearchQueryChange("") }
                    )
                }
                uiState.filteredDevices.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState.searchQuery.isNotBlank() || uiState.selectedType != null) {
                                "No devices match your filters"
                            } else {
                                "No devices yet. Tap + to add one!"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.filteredDevices,
                            key = { it.id }
                        ) { device ->
                            DeviceCard(
                                device = device,
                                onClick = { onNavigateToDeviceDetail(device.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
