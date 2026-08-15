package com.hdb.tourfolio.core.network.dto

data class StockDto(
    val id: Long,
    val name: String,
    val areaCode: String,
    val tier: Int,
    val currentPrice: Long,
    val prevPrice: Long,
    val changeRate: Double,
    val lastUpdated: String,
)
