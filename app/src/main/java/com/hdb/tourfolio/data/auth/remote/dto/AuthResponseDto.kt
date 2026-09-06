package com.hdb.tourfolio.data.auth.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    @SerializedName(value = "userId", alternate = ["id"])
    val id: Long,
    val email: String,
    val nickname: String,
    val token: String,
    val createdAt: String,
    /*
     * 카카오 로그인 응답에만 내려오는 필드 - 신규 가입 여부.
     * 일반 로그인/회원가입 응답에는 없어서 기본값으로 채운다.
     */
    val newMember: Boolean = false,
)
