package com.hdb.tourfolio.data.stock.remote.dto

data class StockDto(
    val id: Long,
    val name: String,
    val areaCode: String,
    val tier: Int,
    val currentPrice: Long,
    val prevPrice: Long,
    val changeRate: Double,
    val lastUpdated: String,
    val regionName: String,
    val address: String,
)

data class StockChartPointDto(
    val date: String,
    val price: Long,
)
