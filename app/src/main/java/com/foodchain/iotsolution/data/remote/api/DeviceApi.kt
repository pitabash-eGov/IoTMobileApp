package com.foodchain.iotsolution.data.remote.api

import com.foodchain.iotsolution.data.remote.dto.device.CreateDeviceRequest
import com.foodchain.iotsolution.data.remote.dto.device.DeviceDto
import com.foodchain.iotsolution.data.remote.dto.device.UpdateDeviceRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DeviceApi {

    @GET("devices")
    suspend fun getAllDevices(): Response<List<DeviceDto>>

    @GET("devices/{id}")
    suspend fun getDeviceById(@Path("id") id: String): Response<DeviceDto>

    @POST("devices")
    suspend fun createDevice(@Body request: CreateDeviceRequest): Response<DeviceDto>

    @PUT("devices/{id}")
    suspend fun updateDevice(
        @Path("id") id: String,
        @Body request: UpdateDeviceRequest
    ): Response<DeviceDto>

    @DELETE("devices/{id}")
    suspend fun deleteDevice(@Path("id") id: String): Response<Unit>
}
