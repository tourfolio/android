package com.hdb.tourfolio.domain.portfolio.usecase

import com.hdb.tourfolio.domain.portfolio.repository.PortfolioRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetCashBalanceUseCase
    @Inject
    constructor(
        private val portfolioRepository: PortfolioRepository,
    ) {
        suspend operator fun invoke(): Long {
            val portfolio = portfolioRepository.observePortfolio().first() ?: portfolioRepository.refreshPortfolio()
            return portfolio.cashBalance
        }
    }
