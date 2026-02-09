package com.foodchain.iotsolution.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.foodchain.iotsolution.domain.model.WeatherData
import com.foodchain.iotsolution.ui.theme.IoTSolutionTheme
import com.foodchain.iotsolution.ui.theme.LocalSpacing

@Composable
fun WeatherWidget(
    weatherData: WeatherData,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${weatherData.temperature.toInt()}\u00B0C",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(spacing.extraSmall))

                Text(
                    text = weatherData.description.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                if (weatherData.city.isNotEmpty()) {
                    Text(
                        text = weatherData.city,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(spacing.small))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.WaterDrop,
                            contentDescription = "Humidity",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                        Text(
                            text = "${weatherData.humidity}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Air,
                            contentDescription = "Wind speed",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                        Text(
                            text = "${weatherData.windSpeed} m/s",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            if (weatherData.iconUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(weatherData.iconUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = weatherData.description,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherWidgetPreview() {
    IoTSolutionTheme {
        WeatherWidget(
            weatherData = WeatherData(
                temperature = 24.0,
                feelsLike = 26.0,
                humidity = 65,
                description = "partly cloudy",
                iconUrl = "",
                windSpeed = 3.5,
                city = "San Francisco",
                country = "US",
                pressure = 1013,
                visibility = 10000
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
