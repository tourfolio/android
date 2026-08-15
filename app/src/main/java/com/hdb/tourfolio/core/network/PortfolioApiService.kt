package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.PortfolioDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PortfolioApiService {
    @Authenticated
    @GET("api/portfolio")
    suspend fun getPortfolio(
        @Query("sort") sort: String,
    ): PortfolioDto
}
