package com.foodchain.iotsolution.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("user")
    val user: UserDto
)

data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_image_url")
    val profileImageUrl: String?
)
