package com.hdb.tourfolio.domain.portfolio.usecase

import com.hdb.tourfolio.domain.portfolio.model.PortfolioSummary
import com.hdb.tourfolio.domain.portfolio.repository.PortfolioRepository
import javax.inject.Inject

class GetPortfolioSummaryUseCase
    @Inject
    constructor(
        private val portfolioRepository: PortfolioRepository,
    ) {
        suspend operator fun invoke(period: String = "1W"): PortfolioSummary = portfolioRepository.getPortfolioSummary(period)
    }
