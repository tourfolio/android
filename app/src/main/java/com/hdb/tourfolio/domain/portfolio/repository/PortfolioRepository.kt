package com.hdb.tourfolio.domain.portfolio.repository

import com.hdb.tourfolio.domain.portfolio.model.Portfolio
import com.hdb.tourfolio.domain.portfolio.model.PortfolioSummary
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    fun observePortfolio(): Flow<Portfolio?>

    suspend fun refreshPortfolio(sort: String = "profit_rate"): Portfolio

    suspend fun getPortfolioSummary(period: String = "1W"): PortfolioSummary
}
