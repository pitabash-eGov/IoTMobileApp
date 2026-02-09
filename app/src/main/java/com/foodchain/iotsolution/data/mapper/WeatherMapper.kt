package com.foodchain.iotsolution.data.mapper

import com.foodchain.iotsolution.core.constants.AppConstants
import com.foodchain.iotsolution.data.remote.dto.weather.WeatherResponse
import com.foodchain.iotsolution.domain.model.WeatherData

object WeatherMapper {

    fun WeatherResponse.toDomain(): WeatherData {
        val weatherInfo = weather.firstOrNull()
        return WeatherData(
            temperature = main.temp,
            feelsLike = main.feelsLike,
            humidity = main.humidity,
            description = weatherInfo?.description ?: "",
            iconUrl = weatherInfo?.icon?.let {
                String.format(AppConstants.WEATHER_ICON_URL, it)
            } ?: "",
            windSpeed = wind.speed,
            city = name,
            country = sys.country,
            pressure = main.pressure,
            visibility = visibility
        )
    }
}
