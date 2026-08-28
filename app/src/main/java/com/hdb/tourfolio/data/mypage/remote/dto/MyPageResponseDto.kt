package com.hdb.tourfolio.data.mypage.remote.dto

data class MyPageResponseDto(
    val nickname: String,
    val balance: Long,
    val cardCount: Int,
    val totalProfitRate: Double,
)