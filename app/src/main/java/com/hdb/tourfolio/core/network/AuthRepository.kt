package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.AuthResponseDto
import com.hdb.tourfolio.core.network.dto.LoginRequestDto
import com.hdb.tourfolio.core.network.dto.SignupRequestDto
import javax.inject.Inject

class AuthRepository
    @Inject
    constructor(
        private val authApiService: AuthApiService,
    ) {
        suspend fun signup(
            email: String,
            password: String,
            nickname: String,
        ): AuthResponseDto = authApiService.signup(SignupRequestDto(email, password, nickname))

        suspend fun login(
            email: String,
            password: String,
        ): AuthResponseDto = authApiService.login(LoginRequestDto(email, password))
    }
