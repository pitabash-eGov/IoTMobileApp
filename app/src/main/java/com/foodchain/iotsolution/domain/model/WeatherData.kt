package com.foodchain.iotsolution.domain.model

data class WeatherData(
    val temperature: Double = 0.0,
    val feelsLike: Double = 0.0,
    val humidity: Int = 0,
    val description: String = "",
    val iconUrl: String = "",
    val windSpeed: Double = 0.0,
    val city: String = "",
    val country: String = "",
    val pressure: Int = 0,
    val visibility: Int = 0
)
