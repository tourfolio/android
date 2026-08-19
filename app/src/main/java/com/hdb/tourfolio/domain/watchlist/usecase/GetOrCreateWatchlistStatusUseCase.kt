package com.hdb.tourfolio.domain.watchlist.usecase

import com.hdb.tourfolio.domain.watchlist.repository.WatchlistRepository
import javax.inject.Inject

class GetOrCreateWatchlistStatusUseCase
    @Inject
    constructor(
        private val watchlistRepository: WatchlistRepository,
    ) {
        suspend operator fun invoke(spotId: Long): Boolean = watchlistRepository.getWatchlist().any { it.spotId == spotId }
    }
