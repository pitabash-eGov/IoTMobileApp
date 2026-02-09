package com.foodchain.iotsolution.data.remote.dto.device

import com.google.gson.annotations.SerializedName

data class DeviceDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("is_online")
    val isOnline: Boolean,
    @SerializedName("location")
    val location: DeviceLocationDto,
    @SerializedName("controls")
    val controls: List<DeviceControlDto>,
    @SerializedName("mqtt_topic_prefix")
    val mqttTopicPrefix: String,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("updated_at")
    val updatedAt: Long
)

data class DeviceLocationDto(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("address")
    val address: String,
    @SerializedName("label")
    val label: String
)

data class DeviceControlDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("control_type")
    val controlType: String,
    @SerializedName("current_value")
    val currentValue: String,
    @SerializedName("min_value")
    val minValue: Float,
    @SerializedName("max_value")
    val maxValue: Float,
    @SerializedName("step")
    val step: Float,
    @SerializedName("options")
    val options: List<String>,
    @SerializedName("mqtt_topic")
    val mqttTopic: String
)
