package com.hdb.tourfolio.domain.stock.model

data class Stock(
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
    val todayTradeVolume: Long,
    val visitorForecast: Double,
    val demandIntensity: Double,
    val resourceDemand: Double,
)
