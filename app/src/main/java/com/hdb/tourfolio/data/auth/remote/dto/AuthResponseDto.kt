package com.hdb.tourfolio.data.auth.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    @SerializedName(value = "userId", alternate = ["id"])
    val id: Long,
    val email: String,
    val nickname: String,
    val token: String,
    val createdAt: String,
)
