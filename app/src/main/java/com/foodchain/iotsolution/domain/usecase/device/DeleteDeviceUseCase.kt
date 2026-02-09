package com.foodchain.iotsolution.domain.usecase.device

import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteDeviceUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    operator fun invoke(id: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val result = repository.deleteDevice(id)
        emit(result)
    }
}
