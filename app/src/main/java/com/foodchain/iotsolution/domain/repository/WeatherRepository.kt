package com.foodchain.iotsolution.domain.repository

import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.model.WeatherData

interface WeatherRepository {
    suspend fun getWeather(lat: Double, lon: Double): Resource<WeatherData>
}
