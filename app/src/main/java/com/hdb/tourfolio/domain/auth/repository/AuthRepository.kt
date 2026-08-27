package com.hdb.tourfolio.domain.auth.repository

import com.hdb.tourfolio.domain.auth.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeCurrentUser(): Flow<User?>

    suspend fun login(
        email: String,
        password: String,
    ): User

    suspend fun signup(
        email: String,
        password: String,
        nickname: String,
    ): User

    suspend fun requireLoggedInUserId(): Long

    suspend fun logout()
}
