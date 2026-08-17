package com.hdb.tourfolio.core.network.dto

data class TradeResponseDto(
    val id: Long,
    val memberId: Int,
    val spotId: Long,
    val type: String,
    val quantity: Int,
    val price: Long,
    val totalAmount: Long,
    val executedAt: String,
    val createdAt: String,
)
