package com.foodchain.iotsolution.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodchain.iotsolution.core.util.Resource
import com.foodchain.iotsolution.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, error = null) }
    }

    fun signUp() {
        val currentState = _uiState.value

        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(error = "Name is required") }
            return
        }
        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(error = "Email is required") }
            return
        }
        if (currentState.password.isBlank()) {
            _uiState.update { it.copy(error = "Password is required") }
            return
        }
        if (currentState.password.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }
        if (currentState.password != currentState.confirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return
        }

        signUpUseCase(
            name = currentState.name.trim(),
            email = currentState.email.trim(),
            password = currentState.password,
            confirmPassword = currentState.confirmPassword
        )
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, isSuccess = true, error = null)
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Sign up failed"
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
