package com.hdb.tourfolio.data.stock

import com.hdb.tourfolio.data.stock.mapper.toDomain
import com.hdb.tourfolio.data.stock.remote.StockApiService
import com.hdb.tourfolio.domain.stock.model.RegionalIndex
import com.hdb.tourfolio.domain.stock.model.Stock
import com.hdb.tourfolio.domain.stock.model.StockChartPoint
import com.hdb.tourfolio.domain.stock.repository.StockRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl
    @Inject
    constructor(
        private val stockApiService: StockApiService,
    ) : StockRepository {
        override suspend fun getStocks(
            region: String,
            keyword: String?,
            tags: List<String>?,
            sortBy: String,
            sortOrder: String,
        ): List<Stock> =
            stockApiService
                .getStocks(
                    region = region,
                    keyword = keyword,
                    tags = tags,
                    sortBy = sortBy,
                    sortOrder = sortOrder,
                ).map { it.toDomain() }

        override suspend fun getTopGainers(): List<Stock> = stockApiService.getTopGainers().map { it.toDomain() }

        override suspend fun getTopLosers(): List<Stock> = stockApiService.getTopLosers().map { it.toDomain() }

        override suspend fun getStockChart(
            spotId: Long,
            period: String,
        ): List<StockChartPoint> = stockApiService.getStockChart(spotId = spotId, period = period).map { it.toDomain() }

        override suspend fun getRegionalIndex(): List<RegionalIndex> = stockApiService.getRegionalIndex().map { it.toDomain() }
    }
