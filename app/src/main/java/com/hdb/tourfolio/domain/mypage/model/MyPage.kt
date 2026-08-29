package com.hdb.tourfolio.domain.mypage.model

data class MyPage(
    val nickname: String,
    val balance: Long,
    val cardCount: Int,
    val totalProfitRate: Double,
)
