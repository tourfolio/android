package com.hdb.tourfolio.data.auth.remote.dto

data class SignupRequestDto(
    val email: String,
    val password: String,
    val nickname: String,
)
