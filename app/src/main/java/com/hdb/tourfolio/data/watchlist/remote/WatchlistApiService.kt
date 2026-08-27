package com.hdb.tourfolio.data.watchlist.remote

import com.hdb.tourfolio.data.common.network.Authenticated
import com.hdb.tourfolio.data.watchlist.remote.dto.WatchlistItemDto
import com.hdb.tourfolio.data.watchlist.remote.dto.WatchlistRegisterResponseDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WatchlistApiService {
    @Authenticated
    @GET("api/watchlist")
    suspend fun getWatchlist(): List<WatchlistItemDto>

    @Authenticated
    @POST("api/watchlist/{spotId}")
    suspend fun registerWatchlist(
        @Path("spotId") spotId: Int,
    ): WatchlistRegisterResponseDto

    @Authenticated
    @DELETE("api/watchlist/{spotId}")
    suspend fun deleteWatchlist(
        @Path("spotId") spotId: Int,
    ): Response<Unit>
}
