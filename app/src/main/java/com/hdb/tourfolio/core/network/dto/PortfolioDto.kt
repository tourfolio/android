package com.hdb.tourfolio.core.network.dto

data class PortfolioDto(
    val memberId: Long,
    val username: String,
    val cashBalance: Long,
    val totalStockValue: Long,
    val totalAssetValue: Long,
    val totalProfitLossRate: Double,
    val items: List<PortfolioItemDto>,
)
