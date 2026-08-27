package com.hdb.tourfolio.domain.watchlist.usecase

import com.hdb.tourfolio.domain.watchlist.repository.WatchlistRepository
import javax.inject.Inject

class ToggleWatchlistUseCase
    @Inject
    constructor(
        private val watchlistRepository: WatchlistRepository,
    ) {
        suspend operator fun invoke(
            spotId: Long,
            currentlyLiked: Boolean,
        ): Result<Boolean> =
            if (currentlyLiked) {
                watchlistRepository.removeFromWatchlist(spotId).map { false }
            } else {
                runCatching { watchlistRepository.addToWatchlist(spotId) }.map { true }
            }
    }
