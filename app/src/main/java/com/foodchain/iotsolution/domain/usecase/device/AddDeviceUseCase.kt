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

class AddDeviceUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    operator fun invoke(
        name: String,
        type: DeviceType,
        location: DeviceLocation,
        controls: List<DeviceControl>
    ): Flow<Resource<Device>> = flow {
        emit(Resource.Loading())
        val device = Device(
            name = name,
            type = type,
            location = location,
            controls = controls
        )
        val result = repository.addDevice(device)
        emit(result)
    }
}
