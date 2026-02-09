package com.foodchain.iotsolution.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foodchain.iotsolution.domain.model.ControlType
import com.foodchain.iotsolution.domain.model.DeviceControl
import com.foodchain.iotsolution.ui.theme.IoTSolutionTheme

@Composable
fun ToggleSwitch(
    control: DeviceControl,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isChecked = control.currentValue == "true"
    var checked by remember(isChecked) { mutableStateOf(isChecked) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = control.name,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = { newValue ->
                checked = newValue
                onToggle(newValue)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ToggleSwitchOnPreview() {
    IoTSolutionTheme {
        ToggleSwitch(
            control = DeviceControl(
                id = "1",
                name = "Power",
                controlType = ControlType.TOGGLE,
                currentValue = "true"
            ),
            onToggle = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ToggleSwitchOffPreview() {
    IoTSolutionTheme {
        ToggleSwitch(
            control = DeviceControl(
                id = "2",
                name = "Night Mode",
                controlType = ControlType.TOGGLE,
                currentValue = "false"
            ),
            onToggle = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
