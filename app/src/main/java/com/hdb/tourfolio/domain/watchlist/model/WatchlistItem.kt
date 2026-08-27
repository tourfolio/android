package com.hdb.tourfolio.domain.watchlist.model

data class WatchlistItem(
    val id: Long,
    val spotId: Long,
    val spotName: String,
    val region: String,
    val theme: String,
    val currentPrice: Long,
    val changeRate: Double,
    val prevPrice: Long,
)

data class WatchlistRegistration(
    val id: Long,
    val spotId: Long,
    val createdAt: String,
)
