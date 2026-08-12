package com.hdb.tourfolio.feature.trade.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.auth.UserSessionManager
import com.hdb.tourfolio.core.network.AuthRepository
import com.hdb.tourfolio.core.network.dto.AuthResponseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthRequestState {
    data object Idle : AuthRequestState

    data object Loading : AuthRequestState

    data class Success(
        val response: AuthResponseDto,
    ) : AuthRequestState

    data class Error(
        val message: String,
    ) : AuthRequestState
}

data class AuthUiState(
    val currentUser: AuthResponseDto? = null,
    val loginState: AuthRequestState = AuthRequestState.Idle,
    val signupState: AuthRequestState = AuthRequestState.Idle,
)

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val userSessionManager: UserSessionManager,
    ) : ViewModel() {
        private val _loginState = MutableStateFlow<AuthRequestState>(AuthRequestState.Idle)
        private val _signupState = MutableStateFlow<AuthRequestState>(AuthRequestState.Idle)

        val uiState: StateFlow<AuthUiState> =
            combine(
                userSessionManager.currentUser,
                _loginState,
                _signupState,
            ) { currentUser, loginState, signupState ->
                AuthUiState(currentUser, loginState, signupState)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuthUiState())

        fun login(
            email: String,
            password: String,
        ) {
            viewModelScope.launch {
                _loginState.value = AuthRequestState.Loading
                _loginState.value =
                    try {
                        val response = authRepository.login(email, password)
                        userSessionManager.setUser(response)
                        AuthRequestState.Success(response)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        AuthRequestState.Error(e.message ?: "로그인에 실패했습니다.")
                    }
            }
        }

        fun signup(
            email: String,
            password: String,
            nickname: String,
        ) {
            viewModelScope.launch {
                _signupState.value = AuthRequestState.Loading
                _signupState.value =
                    try {
                        AuthRequestState.Success(authRepository.signup(email, password, nickname))
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        AuthRequestState.Error(e.message ?: "회원가입에 실패했습니다.")
                    }
            }
        }
    }
