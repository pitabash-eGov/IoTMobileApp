package com.foodchain.iotsolution.domain.usecase.device

import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.model.Device
import com.foodchain.iotsolution.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDeviceByIdUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    operator fun invoke(id: String): Flow<Resource<Device>> = flow {
        emit(Resource.Loading())
        val result = repository.getDeviceById(id)
        emit(result)
    }
}
