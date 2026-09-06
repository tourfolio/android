package com.hdb.tourfolio.domain.stock.repository

import com.hdb.tourfolio.domain.stock.model.RegionalIndex
import com.hdb.tourfolio.domain.stock.model.Stock
import com.hdb.tourfolio.domain.stock.model.StockChartPoint

interface StockRepository {
    suspend fun getStocks(
        region: String = "ALL",
        keyword: String? = null,
        tags: List<String>? = null,
        sortBy: String = "changeRate",
        sortOrder: String = "DESC",
    ): List<Stock>

    suspend fun getTopGainers(): List<Stock>

    suspend fun getTopLosers(): List<Stock>

    suspend fun getStockChart(
        spotId: Long,
        period: String = "1W",
    ): List<StockChartPoint>

    suspend fun getRegionalIndex(): List<RegionalIndex>
}
