package com.foodchain.iotsolution.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foodchain.iotsolution.domain.model.Device
import com.foodchain.iotsolution.domain.model.DeviceType
import com.foodchain.iotsolution.ui.theme.GreenOnline
import com.foodchain.iotsolution.ui.theme.IoTSolutionTheme
import com.foodchain.iotsolution.ui.theme.LocalSpacing
import com.foodchain.iotsolution.ui.theme.RedOffline

@Composable
fun DeviceCard(
    device: Device,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DeviceIcon(
                deviceType = device.type,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(spacing.medium))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(spacing.extraSmall))

                Text(
                    text = device.type.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (device.isOnline) GreenOnline else RedOffline
                        )
                )

                Spacer(modifier = Modifier.width(spacing.small))

                Text(
                    text = if (device.isOnline) "Online" else "Offline",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (device.isOnline) GreenOnline else RedOffline
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceCardOnlinePreview() {
    IoTSolutionTheme {
        DeviceCard(
            device = Device(
                id = "1",
                name = "Living Room Light",
                type = DeviceType.LIGHT,
                isOnline = true
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceCardOfflinePreview() {
    IoTSolutionTheme {
        DeviceCard(
            device = Device(
                id = "2",
                name = "Front Door Lock",
                type = DeviceType.LOCK,
                isOnline = false
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
