package com.hdb.tourfolio.domain.auth.usecase

import com.hdb.tourfolio.domain.auth.model.User
import com.hdb.tourfolio.domain.auth.repository.AuthRepository
import javax.inject.Inject

class SignupUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(
            email: String,
            password: String,
            nickname: String,
        ): User = authRepository.signup(email, password, nickname)
    }
