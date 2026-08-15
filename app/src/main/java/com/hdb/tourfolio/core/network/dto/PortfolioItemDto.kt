package com.hdb.tourfolio.core.network.dto

data class PortfolioItemDto(
    val spotId: Long,
    val spotName: String,
    val quantity: Int,
    val averagePurchasePrice: Long,
    val currentPrice: Long,
    val evaluationAmount: Long,
    val profitLossRate: Double,
)
