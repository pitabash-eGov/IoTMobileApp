package com.foodchain.iotsolution.domain.usecase.auth

import android.util.Patterns
import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.model.User
import com.foodchain.iotsolution.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Flow<Resource<User>> = flow {
        if (name.isBlank()) {
            emit(Resource.Error("Name cannot be empty"))
            return@flow
        }
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emit(Resource.Error("Please enter a valid email address"))
            return@flow
        }
        if (password.isBlank() || password.length < 6) {
            emit(Resource.Error("Password must be at least 6 characters"))
            return@flow
        }
        if (password != confirmPassword) {
            emit(Resource.Error("Passwords do not match"))
            return@flow
        }

        emit(Resource.Loading())
        val result = repository.signUp(name.trim(), email.trim(), password)
        emit(result)
    }
}
