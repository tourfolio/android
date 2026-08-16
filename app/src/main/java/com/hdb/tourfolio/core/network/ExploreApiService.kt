package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.ExploreCardDto
import com.hdb.tourfolio.core.network.dto.ExploreMainCardDto
import retrofit2.http.GET

interface ExploreApiService {
    @Authenticated
    @GET("api/v1/explore/main-cards")
    suspend fun getMainCards(): List<ExploreMainCardDto>

    @Authenticated
    @GET("api/v1/explore/cards")
    suspend fun getCards(): List<ExploreCardDto>

    @Authenticated
    @GET("api/v1/explore/trending")
    suspend fun getTrendingCards(): List<ExploreCardDto>
}