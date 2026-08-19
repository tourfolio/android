package com.hdb.tourfolio.data.portfolio.remote.dto

data class PortfolioDto(
    val memberId: Long,
    val username: String,
    val cashBalance: Long,
    val totalStockValue: Long,
    val totalAssetValue: Long,
    val totalProfitLossRate: Double,
    val items: List<PortfolioItemDto>,
)

data class PortfolioItemDto(
    val spotId: Long,
    val spotName: String,
    val quantity: Int,
    val averagePurchasePrice: Long,
    val currentPrice: Long,
    val evaluationAmount: Long,
    val profitLossRate: Double,
)

data class PortfolioSummaryDto(
    val totalAsset: Long,
    val totalEvaluation: Long,
    val totalPurchase: Long,
    val totalProfitLoss: Long,
    val profitRate: Double,
    val cashBalance: Long,
    val assetHistory: List<PortfolioAssetHistoryPointDto>,
)

data class PortfolioAssetHistoryPointDto(
    val date: String,
    val totalAsset: Long,
)
