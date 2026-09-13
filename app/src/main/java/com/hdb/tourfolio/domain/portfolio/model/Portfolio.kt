package com.hdb.tourfolio.domain.portfolio.model

data class Portfolio(
    val memberId: Long,
    val username: String,
    val cashBalance: Long,
    val totalStockValue: Long,
    val totalAssetValue: Long,
    val totalProfitLossRate: Double,
    val monthlyProfit: Long,
    val monthlyProfitRate: Double,
    val items: List<PortfolioItem>,
)

data class PortfolioItem(
    val spotId: Long,
    val spotName: String,
    val quantity: Int,
    val averagePurchasePrice: Double,
    val currentPrice: Long,
    val evaluationAmount: Long,
    val profitLossRate: Double,
)

data class PortfolioSummary(
    val totalAsset: Long,
    val totalEvaluation: Long,
    val totalPurchase: Long,
    val totalProfitLoss: Long,
    val profitRate: Double,
    val cashBalance: Long,
    val assetHistory: List<PortfolioAssetHistoryPoint>,
)

data class PortfolioAssetHistoryPoint(
    val date: String,
    val totalAsset: Long,
)
