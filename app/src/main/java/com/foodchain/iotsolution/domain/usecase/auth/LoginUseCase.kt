package com.foodchain.iotsolution.domain.usecase.auth

import android.util.Patterns
import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.model.User
import com.foodchain.iotsolution.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<Resource<User>> = flow {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emit(Resource.Error("Please enter a valid email address"))
            return@flow
        }
        if (password.isBlank() || password.length < 6) {
            emit(Resource.Error("Password must be at least 6 characters"))
            return@flow
        }

        emit(Resource.Loading())
        val result = repository.login(email.trim(), password)
        emit(result)
    }
}
