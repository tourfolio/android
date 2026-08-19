package com.hdb.tourfolio.domain.trade.model

data class TradeResult(
    val id: Long,
    val spotId: Long,
    val type: TradeType,
    val quantity: Int,
    val price: Long,
    val totalAmount: Long,
    val executedAt: String,
)
