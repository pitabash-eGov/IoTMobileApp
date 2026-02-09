package com.foodchain.iotsolution.domain.usecase.device

import com.foodchain.iotsolution.domain.model.MqttMessage
import com.foodchain.iotsolution.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDeviceUpdatesUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    operator fun invoke(deviceId: String): Flow<MqttMessage> {
        return repository.observeDeviceUpdates(deviceId)
    }
}
