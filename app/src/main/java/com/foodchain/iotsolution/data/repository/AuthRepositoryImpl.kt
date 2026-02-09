package com.foodchain.iotsolution.data.repository

import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.data.local.DataStoreManager
import com.foodchain.iotsolution.data.mapper.DeviceMapper.toDomain
import com.foodchain.iotsolution.data.remote.api.AuthApi
import com.foodchain.iotsolution.data.remote.dto.auth.LoginRequest
import com.foodchain.iotsolution.data.remote.dto.auth.SignUpRequest
import com.foodchain.iotsolution.domain.model.User
import com.foodchain.iotsolution.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val dataStoreManager: DataStoreManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                dataStoreManager.saveAuthTokens(body.token, body.refreshToken)
                dataStoreManager.saveUserData(
                    id = body.user.id,
                    email = body.user.email,
                    name = body.user.name,
                    profileImage = body.user.profileImageUrl ?: ""
                )
                Resource.Success(body.user.toDomain())
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun signUp(name: String, email: String, password: String): Resource<User> {
        return try {
            val response = authApi.register(SignUpRequest(name, email, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                dataStoreManager.saveAuthTokens(body.token, body.refreshToken)
                dataStoreManager.saveUserData(
                    id = body.user.id,
                    email = body.user.email,
                    name = body.user.name,
                    profileImage = body.user.profileImageUrl ?: ""
                )
                Resource.Success(body.user.toDomain())
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun logout() {
        dataStoreManager.clearSession()
    }

    override fun isAuthenticated(): Flow<Boolean> {
        return dataStoreManager.isAuthenticated
    }

    override suspend fun getCurrentUser(): Resource<User> {
        return try {
            val response = authApi.getCurrentUser()
            if (response.isSuccessful) {
                val userDto = response.body()!!
                Resource.Success(userDto.toDomain())
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to get user")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }
}
