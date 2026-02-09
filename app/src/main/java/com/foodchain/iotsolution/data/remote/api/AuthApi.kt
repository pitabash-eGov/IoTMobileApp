package com.foodchain.iotsolution.data.remote.api

import com.foodchain.iotsolution.data.remote.dto.auth.AuthResponse
import com.foodchain.iotsolution.data.remote.dto.auth.LoginRequest
import com.foodchain.iotsolution.data.remote.dto.auth.SignUpRequest
import com.foodchain.iotsolution.data.remote.dto.auth.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserDto>
}
