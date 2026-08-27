package com.hdb.tourfolio.domain.portfolio.usecase

import com.hdb.tourfolio.domain.portfolio.model.Portfolio
import com.hdb.tourfolio.domain.portfolio.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePortfolioUseCase
    @Inject
    constructor(
        private val portfolioRepository: PortfolioRepository,
    ) {
        operator fun invoke(): Flow<Portfolio?> = portfolioRepository.observePortfolio()
    }
