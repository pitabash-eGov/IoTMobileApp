package com.foodchain.iotsolution.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foodchain.iotsolution.domain.model.ControlType
import com.foodchain.iotsolution.domain.model.DeviceControl
import com.foodchain.iotsolution.ui.theme.IoTSolutionTheme
import com.foodchain.iotsolution.ui.theme.LocalSpacing

@Composable
fun ControlSlider(
    control: DeviceControl,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val currentValue = control.currentValue.toFloatOrNull() ?: control.minValue
    var sliderValue by remember(currentValue) { mutableFloatStateOf(currentValue) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = control.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = sliderValue.toInt().toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(spacing.small))

        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onValueChange(sliderValue) },
            valueRange = control.minValue..control.maxValue,
            steps = if (control.step > 0f) {
                ((control.maxValue - control.minValue) / control.step).toInt() - 1
            } else {
                0
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ControlSliderPreview() {
    IoTSolutionTheme {
        ControlSlider(
            control = DeviceControl(
                id = "1",
                name = "Brightness",
                controlType = ControlType.SLIDER,
                currentValue = "75",
                minValue = 0f,
                maxValue = 100f,
                step = 1f
            ),
            onValueChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
