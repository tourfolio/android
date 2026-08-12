package com.hdb.tourfolio.core.network.dto

data class SignupRequestDto(
    val email: String,
    val password: String,
    val nickname: String,
)
