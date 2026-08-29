package com.hdb.tourfolio.domain.home.model

data class Home(
    val portfolio: HomePortfolio,
    val cardCollection: HomeCardCollection,
    val recommendedSpots: List<HomeRecommendedSpot>,
)

data class HomePortfolio(
    val totalAsset: Long,
    val todayProfit: Long,
    val todayProfitRate: Double,
    val totalProfitRate: Double,
    val stockCount: Int,
)

data class HomeCardCollection(
    val ownedCount: Int,
    val totalCount: Int,
    val collectionRate: Double,
)

data class HomeRecommendedSpot(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val description: String,
    val tags: List<String>,
)