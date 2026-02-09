package com.foodchain.iotsolution.domain.usecase.auth

import com.foodchain.iotsolution.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
