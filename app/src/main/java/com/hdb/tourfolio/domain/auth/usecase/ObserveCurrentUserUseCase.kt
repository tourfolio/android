package com.hdb.tourfolio.domain.auth.usecase

import com.hdb.tourfolio.domain.auth.model.User
import com.hdb.tourfolio.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentUserUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        operator fun invoke(): Flow<User?> = authRepository.observeCurrentUser()
    }
