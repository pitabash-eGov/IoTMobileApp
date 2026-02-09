package com.foodchain.iotsolution.di

import com.foodchain.iotsolution.BuildConfig
import com.foodchain.iotsolution.core.constants.AppConstants
import com.foodchain.iotsolution.data.remote.api.AuthApi
import com.foodchain.iotsolution.data.remote.api.DeviceApi
import com.foodchain.iotsolution.data.remote.api.WeatherApi
import com.foodchain.iotsolution.data.remote.interceptor.AuthInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @Named("AuthOkHttpClient")
    fun provideAuthOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(AppConstants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(AppConstants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(AppConstants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("WeatherOkHttpClient")
    fun provideWeatherOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(AppConstants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(AppConstants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("MainRetrofit")
    fun provideMainRetrofit(
        @Named("AuthOkHttpClient") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("WeatherRetrofit")
    fun provideWeatherRetrofit(
        @Named("WeatherOkHttpClient") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.WEATHER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(@Named("MainRetrofit") retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDeviceApi(@Named("MainRetrofit") retrofit: Retrofit): DeviceApi {
        return retrofit.create(DeviceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherApi(@Named("WeatherRetrofit") retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }
}
