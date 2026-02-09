package com.foodchain.iotsolution.domain.usecase.device

import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.model.Device
import com.foodchain.iotsolution.domain.model.DeviceControl
import com.foodchain.iotsolution.domain.model.DeviceLocation
import com.foodchain.iotsolution.domain.model.DeviceType
import com.foodchain.iotsolution.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateDeviceUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    operator fun invoke(
        id: String,
        name: String? = null,
        type: DeviceType? = null,
        location: DeviceLocation? = null,
        controls: List<DeviceControl>? = null
    ): Flow<Resource<Device>> = flow {
        emit(Resource.Loading())
        val currentResult = repository.getDeviceById(id)
        if (currentResult is Resource.Error) {
            emit(Resource.Error(currentResult.message ?: "Device not found"))
            return@flow
        }
        val currentDevice = currentResult.data ?: run {
            emit(Resource.Error("Device not found"))
            return@flow
        }
        val updatedDevice = currentDevice.copy(
            name = name ?: currentDevice.name,
            type = type ?: currentDevice.type,
            location = location ?: currentDevice.location,
            controls = controls ?: currentDevice.controls,
            updatedAt = System.currentTimeMillis()
        )
        val result = repository.updateDevice(updatedDevice)
        emit(result)
    }
}
