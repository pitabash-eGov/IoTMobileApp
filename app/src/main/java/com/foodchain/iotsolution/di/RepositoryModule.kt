package com.foodchain.iotsolution.di

import com.foodchain.iotsolution.data.repository.AuthRepositoryImpl
import com.foodchain.iotsolution.data.repository.DeviceRepositoryImpl
import com.foodchain.iotsolution.data.repository.WeatherRepositoryImpl
import com.foodchain.iotsolution.domain.repository.AuthRepository
import com.foodchain.iotsolution.domain.repository.DeviceRepository
import com.foodchain.iotsolution.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDeviceRepository(impl: DeviceRepositoryImpl): DeviceRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository
}
