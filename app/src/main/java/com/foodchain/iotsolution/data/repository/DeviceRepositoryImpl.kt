package com.foodchain.iotsolution.data.repository

import com.foodchain.iotsolution.core.constants.AppConstants
import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.data.mapper.DeviceMapper.toDto
import com.foodchain.iotsolution.data.mapper.DeviceMapper.toDomain
import com.foodchain.iotsolution.data.mapper.DeviceMapper.toLocationDto
import com.foodchain.iotsolution.data.remote.api.DeviceApi
import com.foodchain.iotsolution.data.remote.dto.device.CreateDeviceRequest
import com.foodchain.iotsolution.data.remote.dto.device.UpdateDeviceRequest
import com.foodchain.iotsolution.data.remote.mqtt.MqttManager
import com.foodchain.iotsolution.domain.model.Device
import com.foodchain.iotsolution.domain.model.MqttMessage
import com.foodchain.iotsolution.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val deviceApi: DeviceApi,
    private val mqttManager: MqttManager
) : DeviceRepository {

    override suspend fun getAllDevices(): Resource<List<Device>> {
        return try {
            val response = deviceApi.getAllDevices()
            if (response.isSuccessful) {
                val devices = response.body()?.map { it.toDomain() } ?: emptyList()
                Resource.Success(devices)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to fetch devices")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun getDeviceById(id: String): Resource<Device> {
        return try {
            val response = deviceApi.getDeviceById(id)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to fetch device")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun addDevice(device: Device): Resource<Device> {
        return try {
            val request = CreateDeviceRequest(
                name = device.name,
                type = device.type.name,
                location = device.toLocationDto(),
                controls = device.controls.map { it.toDto() },
                mqttTopicPrefix = device.mqttTopicPrefix
            )
            val response = deviceApi.createDevice(request)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to add device")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun updateDevice(device: Device): Resource<Device> {
        return try {
            val request = UpdateDeviceRequest(
                name = device.name,
                type = device.type.name,
                location = device.toLocationDto(),
                controls = device.controls.map { it.toDto() },
                mqttTopicPrefix = device.mqttTopicPrefix
            )
            val response = deviceApi.updateDevice(device.id, request)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to update device")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun deleteDevice(id: String): Resource<Unit> {
        return try {
            val response = deviceApi.deleteDevice(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to delete device")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun sendControlCommand(
        deviceId: String,
        controlId: String,
        value: String
    ): Resource<Unit> {
        return try {
            val topic = String.format(AppConstants.TOPIC_DEVICE_CONTROL, deviceId)
            val payload = """{"controlId":"$controlId","value":"$value"}"""
            mqttManager.publish(topic, payload)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send command")
        }
    }

    override fun observeDeviceUpdates(deviceId: String): Flow<MqttMessage> {
        val statusTopic = String.format(AppConstants.TOPIC_DEVICE_STATUS, deviceId)
        val telemetryTopic = String.format(AppConstants.TOPIC_DEVICE_TELEMETRY, deviceId)
        mqttManager.subscribe(statusTopic)
        mqttManager.subscribe(telemetryTopic)
        return mqttManager.messages.filter { msg ->
            msg.topic.startsWith("devices/$deviceId/")
        }
    }
}
