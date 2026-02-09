package com.foodchain.iotsolution.di

import android.content.Context
import com.foodchain.iotsolution.data.remote.mqtt.MqttManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MqttModule {

    @Provides
    @Singleton
    fun provideMqttManager(@ApplicationContext context: Context): MqttManager {
        return MqttManager(context)
    }
}
