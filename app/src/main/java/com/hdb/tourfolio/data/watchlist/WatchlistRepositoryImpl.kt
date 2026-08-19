package com.hdb.tourfolio.data.watchlist

import com.hdb.tourfolio.data.watchlist.mapper.toDomain
import com.hdb.tourfolio.data.watchlist.remote.WatchlistApiService
import com.hdb.tourfolio.domain.watchlist.model.WatchlistItem
import com.hdb.tourfolio.domain.watchlist.model.WatchlistRegistration
import com.hdb.tourfolio.domain.watchlist.repository.WatchlistRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepositoryImpl
    @Inject
    constructor(
        private val watchlistApiService: WatchlistApiService,
    ) : WatchlistRepository {
        override suspend fun getWatchlist(): List<WatchlistItem> = watchlistApiService.getWatchlist().map { it.toDomain() }

        override suspend fun addToWatchlist(spotId: Long): WatchlistRegistration =
            watchlistApiService.registerWatchlist(spotId = spotId.toInt()).toDomain()

        override suspend fun removeFromWatchlist(spotId: Long): Result<Unit> {
            val response = watchlistApiService.deleteWatchlist(spotId = spotId.toInt())

            return if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("관심 목록 삭제에 실패했습니다: HTTP ${response.code()}"))
            }
        }
    }
