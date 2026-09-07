package com.hdb.tourfolio.data.trade.remote.dto

data class TradeRequestDto(
    val memberId: Int,
    val spotId: Long,
    val type: String,
    val quantity: Int,
)

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
    val realizedProfit: Long?,
)
