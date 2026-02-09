package com.foodchain.iotsolution

import android.app.Application
import com.foodchain.iotsolution.data.service.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class IoTSolutionApp : Application() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createChannels()
    }
}
