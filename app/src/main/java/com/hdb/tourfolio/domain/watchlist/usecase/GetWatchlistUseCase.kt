package com.hdb.tourfolio.domain.watchlist.usecase

import com.hdb.tourfolio.domain.watchlist.model.WatchlistItem
import com.hdb.tourfolio.domain.watchlist.repository.WatchlistRepository
import javax.inject.Inject

class GetWatchlistUseCase
    @Inject
    constructor(
        private val watchlistRepository: WatchlistRepository,
    ) {
        suspend operator fun invoke(): List<WatchlistItem> = watchlistRepository.getWatchlist()
    }
