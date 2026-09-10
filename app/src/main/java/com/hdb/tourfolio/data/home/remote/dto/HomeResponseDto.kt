package com.hdb.tourfolio.data.home.remote.dto

data class HomeResponseDto(
    val portfolio: HomePortfolioDto,
    val recommendedSpots: List<HomeRecommendedSpotDto>,
)

data class HomePortfolioDto(
    val totalAsset: Long,
    val todayProfit: Long,
    val todayProfitRate: Double,
    val totalProfitRate: Double,
    val pointBalance: Long,
    val stockCount: Int,
)

data class HomeRecommendedSpotDto(
    val spotId: Long,
    val name: String,
    val imageUrl: String,
    val hasImage: Boolean,
    val description: String,
    val tags: List<String>,
)
