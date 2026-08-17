package com.hdb.tourfolio.core.network.dto

data class TradeRequestDto(
    val memberId: Int,
    val spotId: Long,
    val type: String,
    val quantity: Int,
)
