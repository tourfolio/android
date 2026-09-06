package com.hdb.tourfolio.data.stock.remote.dto

data class StockDto(
    val id: Long,
    val name: String,
    val areaCode: String,
    val regionName: String,
    val tier: Int,
    val currentPrice: Long,
    val prevPrice: Long,
    val changeRate: Double,
    val lastUpdated: String,
    val address: String,
    val todayTradeVolume: Long,
    val visitorForecast: Double,
    val demandIntensity: Double,
    val resourceDemand: Double,
)

data class StockChartPointDto(
    val date: String,
    val price: Long,
)
