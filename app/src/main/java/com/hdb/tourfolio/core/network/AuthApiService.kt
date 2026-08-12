package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.AuthResponseDto
import com.hdb.tourfolio.core.network.dto.LoginRequestDto
import com.hdb.tourfolio.core.network.dto.SignupRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/signup")
    suspend fun signup(
        @Body request: SignupRequestDto,
    ): AuthResponseDto

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto,
    ): AuthResponseDto
}
