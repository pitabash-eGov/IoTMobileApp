package com.foodchain.iotsolution.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foodchain.iotsolution.domain.model.ControlType
import com.foodchain.iotsolution.domain.model.DeviceControl
import com.foodchain.iotsolution.ui.theme.IoTSolutionTheme

@Composable
fun ControlButton(
    control: DeviceControl,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = control.name,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ControlButtonPreview() {
    IoTSolutionTheme {
        ControlButton(
            control = DeviceControl(
                id = "1",
                name = "Restart Device",
                controlType = ControlType.BUTTON,
                currentValue = ""
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
