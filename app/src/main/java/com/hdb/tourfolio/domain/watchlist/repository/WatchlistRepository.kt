package com.hdb.tourfolio.domain.watchlist.repository

import com.hdb.tourfolio.domain.watchlist.model.WatchlistItem
import com.hdb.tourfolio.domain.watchlist.model.WatchlistRegistration

interface WatchlistRepository {
    suspend fun getWatchlist(): List<WatchlistItem>

    suspend fun addToWatchlist(spotId: Long): WatchlistRegistration

    suspend fun removeFromWatchlist(spotId: Long): Result<Unit>
}
