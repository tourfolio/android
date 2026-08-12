package com.hdb.tourfolio.core.network.dto

data class AuthResponseDto(
    val id: Long,
    val email: String,
    val nickname: String,
    val token: String,
    val createdAt: String,
)
