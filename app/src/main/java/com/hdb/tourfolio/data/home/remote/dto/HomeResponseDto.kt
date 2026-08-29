package com.hdb.tourfolio.data.home.remote.dto

data class HomeResponseDto(
    val portfolio: HomePortfolioDto,
    val cardCollection: HomeCardCollectionDto,
    val recommendedSpots: List<HomeRecommendedSpotDto>,
)

data class HomePortfolioDto(
    val totalAsset: Long,
    val todayProfit: Long,
    val todayProfitRate: Double,
    val totalProfitRate: Double,
    val stockCount: Int,
)

data class HomeCardCollectionDto(
    val ownedCount: Int,
    val totalCount: Int,
    val collectionRate: Double,
)

data class HomeRecommendedSpotDto(
    val spotId: Long,
    val name: String,
    val imageUrl: String,
    val description: String,
    val tags: List<String>,
)