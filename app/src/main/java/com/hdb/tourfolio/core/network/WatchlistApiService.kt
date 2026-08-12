package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.WatchlistItemDto
import com.hdb.tourfolio.core.network.dto.WatchlistRegisterResponseDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WatchlistApiService {
    @Authenticated
    @GET("api/watchlist")
    suspend fun getWatchlist(
        @Query("userId") userId: Int,
    ): List<WatchlistItemDto>

    @Authenticated
    @POST("api/watchlist/{spotId}")
    suspend fun registerWatchlist(
        @Path("spotId") spotId: Int,
        @Query("userId") userId: Int,
    ): WatchlistRegisterResponseDto

    @Authenticated
    @DELETE("api/watchlist/{spotId}")
    suspend fun deleteWatchlist(
        @Path("spotId") spotId: Int,
        @Query("userId") userId: Int,
    ): Response<Unit>
}
