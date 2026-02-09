package com.foodchain.iotsolution.domain.repository

import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun signUp(name: String, email: String, password: String): Resource<User>
    suspend fun logout()
    fun isAuthenticated(): Flow<Boolean>
    suspend fun getCurrentUser(): Resource<User>
}
