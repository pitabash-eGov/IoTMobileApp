package com.foodchain.iotsolution.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.foodchain.iotsolution.data.local.DataStoreManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootCompletedReceiver"
    }

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        Log.d(TAG, "Boot completed received")

        val isAuthenticated = runBlocking {
            dataStoreManager.isAuthenticated.first()
        }
        val notificationsEnabled = runBlocking {
            dataStoreManager.notificationsEnabled.first()
        }

        if (isAuthenticated && notificationsEnabled) {
            Log.d(TAG, "Starting MQTT foreground service after boot")
            MqttForegroundService.start(context)
        }
    }
}
