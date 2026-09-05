package com.hdb.tourfolio.data.auth

import com.hdb.tourfolio.data.auth.local.SessionLocalDataSource
import com.hdb.tourfolio.data.auth.mapper.toDomain
import com.hdb.tourfolio.data.auth.remote.AuthApiService
import com.hdb.tourfolio.data.auth.remote.dto.LoginRequestDto
import com.hdb.tourfolio.data.auth.remote.dto.SignupRequestDto
import com.hdb.tourfolio.domain.auth.model.NotAuthenticatedException
import com.hdb.tourfolio.domain.auth.model.User
import com.hdb.tourfolio.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val authApiService: AuthApiService,
        private val sessionLocalDataSource: SessionLocalDataSource,
    ) : AuthRepository {
        override fun observeCurrentUser(): Flow<User?> = sessionLocalDataSource.currentSession.map { it?.toDomain() }

        override suspend fun login(
            email: String,
            password: String,
        ): User {
            val response = authApiService.login(LoginRequestDto(email, password))
            sessionLocalDataSource.setSession(response)
            return response.toDomain()
        }

        override suspend fun signup(
            email: String,
            password: String,
            nickname: String,
        ): User = authApiService.signup(SignupRequestDto(email, password, nickname)).toDomain()

        override suspend fun requireLoggedInUserId(): Long = sessionLocalDataSource.getUserId() ?: throw NotAuthenticatedException()

        override suspend fun logout() {
            sessionLocalDataSource.clear()
        }
    }
