package com.hdb.tourfolio.data.portfolio.remote

import com.hdb.tourfolio.data.common.network.Authenticated
import com.hdb.tourfolio.data.portfolio.remote.dto.PortfolioDto
import com.hdb.tourfolio.data.portfolio.remote.dto.PortfolioSummaryDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PortfolioApiService {
    @Authenticated
    @GET("api/portfolio")
    suspend fun getPortfolio(
        @Query("sort") sort: String,
    ): PortfolioDto

    @Authenticated
    @GET("api/portfolio/summary")
    suspend fun getPortfolioSummary(
        @Query("period") period: String = "1W",
    ): PortfolioSummaryDto
}
