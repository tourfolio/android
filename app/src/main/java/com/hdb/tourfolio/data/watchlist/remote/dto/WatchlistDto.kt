package com.hdb.tourfolio.data.watchlist.remote.dto

data class WatchlistItemDto(
    val id: Long,
    val spotId: Long,
    val spotName: String,
    val region: String,
    val theme: String,
    val currentPrice: Long,
    val changeRate: Double,
    val createdAt: String,
)

data class WatchlistRegisterResponseDto(
    val id: Long,
    val userId: Int,
    val spotId: Long,
    val createdAt: String,
)
