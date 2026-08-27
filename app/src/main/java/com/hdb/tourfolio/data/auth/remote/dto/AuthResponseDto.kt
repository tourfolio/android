package com.hdb.tourfolio.data.auth.remote.dto

data class AuthResponseDto(
    val id: Long,
    val email: String,
    val nickname: String,
    val token: String,
    val createdAt: String,
)
