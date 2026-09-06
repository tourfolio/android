package com.hdb.tourfolio.domain.auth.usecase

import com.hdb.tourfolio.domain.auth.model.User
import com.hdb.tourfolio.domain.auth.repository.AuthRepository
import javax.inject.Inject

class LoginWithKakaoUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(code: String): User = authRepository.loginWithKakao(code)
    }
