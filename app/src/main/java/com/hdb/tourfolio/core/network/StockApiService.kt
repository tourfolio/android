package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.StockDto
import retrofit2.http.GET

interface StockApiService {
    @GET("api/stocks")
    suspend fun getStocks(): List<StockDto>

    @GET("api/stocks/top-gainers")
    suspend fun getTopGainers(): List<StockDto>

    @GET("api/stocks/top-losers")
    suspend fun getTopLosers(): List<StockDto>
}
