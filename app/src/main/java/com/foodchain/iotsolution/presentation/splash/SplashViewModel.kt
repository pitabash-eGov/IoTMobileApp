package com.foodchain.iotsolution.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodchain.iotsolution.domain.usecase.auth.GetAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getAuthStateUseCase: GetAuthStateUseCase
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow<Boolean?>(null)
    val isAuthenticated: StateFlow<Boolean?> = _isAuthenticated.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        getAuthStateUseCase()
            .onEach { isLoggedIn ->
                _isAuthenticated.value = isLoggedIn
            }
            .catch {
                _isAuthenticated.value = false
            }
            .launchIn(viewModelScope)
    }
}
