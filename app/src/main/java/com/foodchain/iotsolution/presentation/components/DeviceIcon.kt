package com.foodchain.iotsolution.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.DevicesOther
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.ToggleOn
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.foodchain.iotsolution.domain.model.DeviceType
import com.foodchain.iotsolution.ui.theme.IoTSolutionTheme

@Composable
fun DeviceIcon(
    deviceType: DeviceType,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    val icon = when (deviceType) {
        DeviceType.LIGHT -> Icons.Outlined.Lightbulb
        DeviceType.THERMOSTAT -> Icons.Outlined.Thermostat
        DeviceType.SWITCH -> Icons.Outlined.ToggleOn
        DeviceType.SENSOR -> Icons.Outlined.Sensors
        DeviceType.CAMERA -> Icons.Outlined.Videocam
        DeviceType.LOCK -> Icons.Outlined.Lock
        DeviceType.FAN -> Icons.Outlined.Air
        DeviceType.CUSTOM -> Icons.Outlined.DevicesOther
    }

    Icon(
        imageVector = icon,
        contentDescription = deviceType.displayName,
        modifier = modifier,
        tint = tint
    )
}

@Preview(showBackground = true)
@Composable
private fun DeviceIconLightPreview() {
    IoTSolutionTheme {
        DeviceIcon(deviceType = DeviceType.LIGHT)
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceIconThermostatPreview() {
    IoTSolutionTheme {
        DeviceIcon(deviceType = DeviceType.THERMOSTAT)
    }
}
