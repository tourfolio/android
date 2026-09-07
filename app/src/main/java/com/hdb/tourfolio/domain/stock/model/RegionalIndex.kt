package com.hdb.tourfolio.domain.stock.model

data class RegionalIndex(
    val region: String,
    val averageChangeRate: Double,
    val spotCount: Int,
)
