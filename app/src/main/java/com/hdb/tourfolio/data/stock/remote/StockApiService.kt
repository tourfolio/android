package com.hdb.tourfolio.data.stock.remote

import com.hdb.tourfolio.data.stock.remote.dto.RegionalIndexDto
import com.hdb.tourfolio.data.stock.remote.dto.StockChartPointDto
import com.hdb.tourfolio.data.stock.remote.dto.StockDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StockApiService {
    @GET("api/stocks")
    suspend fun getStocks(
        @Query("region") region: String = "ALL",
        @Query("keyword") keyword: String? = null,
        @Query("tags") tags: List<String>? = null,
        @Query("sortBy") sortBy: String = "changeRate",
        @Query("sortOrder") sortOrder: String = "DESC",
    ): List<StockDto>

    @GET("api/stocks/top-gainers")
    suspend fun getTopGainers(): List<StockDto>

    @GET("api/stocks/top-losers")
    suspend fun getTopLosers(): List<StockDto>

    @GET("api/stocks/{spotId}/chart")
    suspend fun getStockChart(
        @Path("spotId") spotId: Long,
        @Query("period") period: String = "1W",
    ): List<StockChartPointDto>

    @GET("api/stocks/regional-index")
    suspend fun getRegionalIndex(): List<RegionalIndexDto>
}
