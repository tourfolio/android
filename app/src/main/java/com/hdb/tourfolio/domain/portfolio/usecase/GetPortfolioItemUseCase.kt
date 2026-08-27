package com.hdb.tourfolio.domain.portfolio.usecase

import com.hdb.tourfolio.domain.portfolio.model.PortfolioItem
import com.hdb.tourfolio.domain.portfolio.repository.PortfolioRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetPortfolioItemUseCase
    @Inject
    constructor(
        private val portfolioRepository: PortfolioRepository,
    ) {
        suspend operator fun invoke(spotId: Long): PortfolioItem? {
            val portfolio = portfolioRepository.observePortfolio().first() ?: portfolioRepository.refreshPortfolio()
            return portfolio.items.firstOrNull { it.spotId == spotId }
        }
    }
