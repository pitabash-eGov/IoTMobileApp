package com.foodchain.iotsolution.domain.repository

import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.model.Device
import com.foodchain.iotsolution.domain.model.MqttMessage
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    suspend fun getAllDevices(): Resource<List<Device>>
    suspend fun getDeviceById(id: String): Resource<Device>
    suspend fun addDevice(device: Device): Resource<Device>
    suspend fun updateDevice(device: Device): Resource<Device>
    suspend fun deleteDevice(id: String): Resource<Unit>
    suspend fun sendControlCommand(deviceId: String, controlId: String, value: String): Resource<Unit>
    fun observeDeviceUpdates(deviceId: String): Flow<MqttMessage>
}
