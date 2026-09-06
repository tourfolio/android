package com.hdb.tourfolio.feature.auth.presentation

import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.auth.model.User
import com.hdb.tourfolio.domain.auth.usecase.LoginUseCase
import com.hdb.tourfolio.domain.auth.usecase.LoginWithKakaoUseCase
import com.hdb.tourfolio.domain.auth.usecase.ObserveCurrentUserUseCase
import com.hdb.tourfolio.domain.auth.usecase.SignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthRequestState {
    data object Idle : AuthRequestState

    data object Loading : AuthRequestState

    data class Success(
        val user: User,
    ) : AuthRequestState

    data class Error(
        val message: String,
    ) : AuthRequestState
}

sealed interface AuthIntent : MviIntent {
    data class Login(
        val email: String,
        val password: String,
    ) : AuthIntent

    data class Signup(
        val email: String,
        val password: String,
        val nickname: String,
    ) : AuthIntent

    data class LoginWithKakao(
        val code: String,
    ) : AuthIntent

    data class KakaoLoginFailed(
        val message: String,
    ) : AuthIntent
}

data class AuthState(
    val currentUser: User? = null,
    val loginState: AuthRequestState = AuthRequestState.Idle,
    val signupState: AuthRequestState = AuthRequestState.Idle,
) : MviState

sealed interface AuthEffect : MviEffect

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
        private val signupUseCase: SignupUseCase,
        private val loginWithKakaoUseCase: LoginWithKakaoUseCase,
        observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    ) : MviViewModel<AuthIntent, AuthState, AuthEffect>(AuthState()) {
        init {
            viewModelScope.launch {
                observeCurrentUserUseCase().collect { user ->
                    setState { copy(currentUser = user) }
                }
            }
        }

        override suspend fun handleIntent(intent: AuthIntent) {
            when (intent) {
                is AuthIntent.Login -> login(intent.email, intent.password)
                is AuthIntent.Signup -> signup(intent.email, intent.password, intent.nickname)
                is AuthIntent.LoginWithKakao -> loginWithKakao(intent.code)
                is AuthIntent.KakaoLoginFailed -> setState { copy(loginState = AuthRequestState.Error(intent.message)) }
            }
        }

        private suspend fun login(
            email: String,
            password: String,
        ) {
            setState { copy(loginState = AuthRequestState.Loading) }
            val result =
                try {
                    AuthRequestState.Success(loginUseCase(email, password))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    AuthRequestState.Error(e.message ?: "로그인에 실패했습니다.")
                }
            setState { copy(loginState = result) }
        }

        private suspend fun loginWithKakao(code: String) {
            setState { copy(loginState = AuthRequestState.Loading) }
            val result =
                try {
                    AuthRequestState.Success(loginWithKakaoUseCase(code))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    AuthRequestState.Error(e.message ?: "카카오 로그인에 실패했습니다.")
                }
            setState { copy(loginState = result) }
        }

        private suspend fun signup(
            email: String,
            password: String,
            nickname: String,
        ) {
            setState { copy(signupState = AuthRequestState.Loading) }
            val result =
                try {
                    AuthRequestState.Success(signupUseCase(email, password, nickname))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    AuthRequestState.Error(e.message ?: "회원가입에 실패했습니다.")
                }
            setState { copy(signupState = result) }
        }
    }
