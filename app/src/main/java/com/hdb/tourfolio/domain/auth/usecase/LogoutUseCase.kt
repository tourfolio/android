package com.hdb.tourfolio.domain.auth.usecase

import com.hdb.tourfolio.domain.auth.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke() = authRepository.logout()
    }
