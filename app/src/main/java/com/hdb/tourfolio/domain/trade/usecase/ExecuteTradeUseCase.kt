package com.hdb.tourfolio.domain.trade.usecase

import com.hdb.tourfolio.domain.portfolio.repository.PortfolioRepository
import com.hdb.tourfolio.domain.trade.model.TradeResult
import com.hdb.tourfolio.domain.trade.model.TradeType
import com.hdb.tourfolio.domain.trade.repository.TradeRepository
import javax.inject.Inject

class ExecuteTradeUseCase
    @Inject
    constructor(
        private val tradeRepository: TradeRepository,
        private val portfolioRepository: PortfolioRepository,
    ) {
        suspend operator fun invoke(
            spotId: Long,
            type: TradeType,
            quantity: Int,
        ): TradeResult {
            val result = tradeRepository.trade(spotId, type, quantity)
            portfolioRepository.refreshPortfolio()
            return result
        }
    }
