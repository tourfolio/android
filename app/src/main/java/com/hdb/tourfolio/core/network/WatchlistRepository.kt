package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.WatchlistItemDto
import com.hdb.tourfolio.core.network.dto.WatchlistRegisterResponseDto
import javax.inject.Inject

class WatchlistRepository
    @Inject
    constructor(
        private val watchlistApiService: WatchlistApiService,
    ) {
        suspend fun getWatchlist(userId: Int = DEFAULT_USER_ID): List<WatchlistItemDto> = watchlistApiService.getWatchlist(userId)

        suspend fun registerWatchlist(
            spotId: Int,
            userId: Int = DEFAULT_USER_ID,
        ): WatchlistRegisterResponseDto = watchlistApiService.registerWatchlist(spotId = spotId, userId = userId)

        suspend fun deleteWatchlist(
            spotId: Int,
            userId: Int = DEFAULT_USER_ID,
        ) {
            watchlistApiService.deleteWatchlist(spotId = spotId, userId = userId)
        }

        companion object {
            private const val DEFAULT_USER_ID = 4
        }
    }
