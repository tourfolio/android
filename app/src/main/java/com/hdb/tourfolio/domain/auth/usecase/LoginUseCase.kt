package com.hdb.tourfolio.domain.auth.usecase

import com.hdb.tourfolio.domain.auth.model.User
import com.hdb.tourfolio.domain.auth.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(
            email: String,
            password: String,
        ): User = authRepository.login(email, password)
    }
