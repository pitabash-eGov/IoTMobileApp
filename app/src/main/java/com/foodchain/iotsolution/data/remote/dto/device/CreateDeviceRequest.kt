package com.foodchain.iotsolution.data.remote.dto.device

import com.google.gson.annotations.SerializedName

data class CreateDeviceRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("location")
    val location: DeviceLocationDto,
    @SerializedName("controls")
    val controls: List<DeviceControlDto>,
    @SerializedName("mqtt_topic_prefix")
    val mqttTopicPrefix: String
)
