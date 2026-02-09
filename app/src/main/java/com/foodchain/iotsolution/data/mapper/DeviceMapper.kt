package com.foodchain.iotsolution.data.mapper

import com.foodchain.iotsolution.data.remote.dto.auth.UserDto
import com.foodchain.iotsolution.data.remote.dto.device.DeviceControlDto
import com.foodchain.iotsolution.data.remote.dto.device.DeviceDto
import com.foodchain.iotsolution.data.remote.dto.device.DeviceLocationDto
import com.foodchain.iotsolution.domain.model.ControlType
import com.foodchain.iotsolution.domain.model.Device
import com.foodchain.iotsolution.domain.model.DeviceControl
import com.foodchain.iotsolution.domain.model.DeviceLocation
import com.foodchain.iotsolution.domain.model.DeviceType
import com.foodchain.iotsolution.domain.model.User

object DeviceMapper {

    fun DeviceDto.toDomain(): Device {
        return Device(
            id = id,
            name = name,
            type = try {
                DeviceType.valueOf(type.uppercase())
            } catch (e: IllegalArgumentException) {
                DeviceType.CUSTOM
            },
            isOnline = isOnline,
            location = location.toDomain(),
            controls = controls.map { it.toDomain() },
            mqttTopicPrefix = mqttTopicPrefix,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun DeviceLocationDto.toDomain(): DeviceLocation {
        return DeviceLocation(
            latitude = latitude,
            longitude = longitude,
            address = address,
            label = label
        )
    }

    fun DeviceControlDto.toDomain(): DeviceControl {
        return DeviceControl(
            id = id,
            name = name,
            controlType = try {
                ControlType.valueOf(controlType.uppercase())
            } catch (e: IllegalArgumentException) {
                ControlType.TOGGLE
            },
            currentValue = currentValue,
            minValue = minValue,
            maxValue = maxValue,
            step = step,
            options = options,
            mqttTopic = mqttTopic
        )
    }

    fun UserDto.toDomain(): User {
        return User(
            id = id,
            email = email,
            name = name,
            profileImageUrl = profileImageUrl ?: ""
        )
    }

    fun Device.toLocationDto(): DeviceLocationDto {
        return DeviceLocationDto(
            latitude = location.latitude,
            longitude = location.longitude,
            address = location.address,
            label = location.label
        )
    }

    fun DeviceControl.toDto(): DeviceControlDto {
        return DeviceControlDto(
            id = id,
            name = name,
            controlType = controlType.name,
            currentValue = currentValue,
            minValue = minValue,
            maxValue = maxValue,
            step = step,
            options = options,
            mqttTopic = mqttTopic
        )
    }
}
