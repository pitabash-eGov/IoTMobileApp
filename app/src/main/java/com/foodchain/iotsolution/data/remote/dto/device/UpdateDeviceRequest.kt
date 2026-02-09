package com.foodchain.iotsolution.data.remote.dto.device

import com.google.gson.annotations.SerializedName

data class UpdateDeviceRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("location")
    val location: DeviceLocationDto? = null,
    @SerializedName("controls")
    val controls: List<DeviceControlDto>? = null,
    @SerializedName("mqtt_topic_prefix")
    val mqttTopicPrefix: String? = null
)
