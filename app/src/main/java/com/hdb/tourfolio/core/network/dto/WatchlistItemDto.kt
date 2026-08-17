package com.hdb.tourfolio.core.network.dto

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
