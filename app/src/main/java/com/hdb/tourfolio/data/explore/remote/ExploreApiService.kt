package com.hdb.tourfolio.data.explore.remote

import com.hdb.tourfolio.data.common.network.Authenticated
import com.hdb.tourfolio.data.explore.remote.dto.ExploreCardDto
import com.hdb.tourfolio.data.explore.remote.dto.ExploreHubDto
import com.hdb.tourfolio.data.explore.remote.dto.ExploreMainCardDto
import com.hdb.tourfolio.data.explore.remote.dto.ExploreSearchDto
import com.hdb.tourfolio.data.explore.remote.dto.ExploreSpotDetailDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

    @Authenticated
    @GET("api/v1/explore/hub")
    suspend fun getHub(): ExploreHubDto

    @Authenticated
    @GET("api/v1/explore/search")
    suspend fun searchSpots(
        @Query("keyword")
        keyword: String? = null,
        @Query("regions")
        regions: List<String>? = null,
        @Query("themes")
        themes: List<String>? = null,
        @Query("tags")
        tags: List<String>? = null,
    ): ExploreSearchDto

    @Authenticated
    @GET("api/v1/explore/spots/{spotId}")
    suspend fun getSpotDetail(
        @Path("spotId") spotId: Long,
    ): ExploreSpotDetailDto
}
