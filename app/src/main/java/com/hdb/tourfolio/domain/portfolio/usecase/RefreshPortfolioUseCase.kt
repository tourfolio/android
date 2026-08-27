package com.hdb.tourfolio.domain.portfolio.usecase

import com.hdb.tourfolio.domain.portfolio.model.Portfolio
import com.hdb.tourfolio.domain.portfolio.repository.PortfolioRepository
import javax.inject.Inject

class RefreshPortfolioUseCase
    @Inject
    constructor(
        private val portfolioRepository: PortfolioRepository,
    ) {
        suspend operator fun invoke(sort: String = "profit_rate"): Portfolio = portfolioRepository.refreshPortfolio(sort)
    }
