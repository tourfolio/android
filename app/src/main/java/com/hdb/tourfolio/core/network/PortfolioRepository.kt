package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.PortfolioDto
import javax.inject.Inject

class PortfolioRepository
    @Inject
    constructor(
        private val portfolioApiService: PortfolioApiService,
    ) {
        suspend fun getPortfolio(sort: String = DEFAULT_SORT): PortfolioDto = portfolioApiService.getPortfolio(sort)

        companion object {
            private const val DEFAULT_SORT = "profit_rate"
        }
    }
