package com.foodchain.iotsolution.domain.usecase.weather

import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.model.WeatherData
import com.foodchain.iotsolution.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(lat: Double, lon: Double): Flow<Resource<WeatherData>> = flow {
        emit(Resource.Loading())
        val result = repository.getWeather(lat, lon)
        emit(result)
    }
}
