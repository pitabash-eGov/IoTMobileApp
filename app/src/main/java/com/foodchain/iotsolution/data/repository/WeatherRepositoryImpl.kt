package com.foodchain.iotsolution.data.repository

import com.foodchain.iotsolution.BuildConfig
import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.data.mapper.WeatherMapper.toDomain
import com.foodchain.iotsolution.data.remote.api.WeatherApi
import com.foodchain.iotsolution.domain.model.WeatherData
import com.foodchain.iotsolution.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi
) : WeatherRepository {

    override suspend fun getWeather(lat: Double, lon: Double): Resource<WeatherData> {
        return try {
            val response = weatherApi.getWeather(lat, lon, BuildConfig.WEATHER_API_KEY)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to fetch weather")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }
}
