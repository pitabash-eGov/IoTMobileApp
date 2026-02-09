package com.foodchain.iotsolution.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.foodchain.iotsolution.presentation.navigation.Screen
import com.foodchain.iotsolution.ui.theme.IoTSolutionTheme

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(
            label = "Home",
            icon = Icons.Filled.Home,
            route = Screen.Home.route
        ),
        BottomNavItem(
            label = "Devices",
            icon = Icons.Filled.DevicesOther,
            route = Screen.DeviceList.route
        ),
        BottomNavItem(
            label = "Map",
            icon = Icons.Filled.Map,
            route = Screen.Map.route
        ),
        BottomNavItem(
            label = "Settings",
            icon = Icons.Filled.Settings,
            route = Screen.Settings.route
        )
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavBarPreview() {
    IoTSolutionTheme {
        BottomNavBar(
            currentRoute = Screen.Home.route,
            onNavigate = {}
        )
    }
}
