package com.hdb.tourfolio.data.auth.remote

import com.hdb.tourfolio.data.auth.remote.dto.AuthResponseDto
import com.hdb.tourfolio.data.auth.remote.dto.LoginRequestDto
import com.hdb.tourfolio.data.auth.remote.dto.SignupRequestDto
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
