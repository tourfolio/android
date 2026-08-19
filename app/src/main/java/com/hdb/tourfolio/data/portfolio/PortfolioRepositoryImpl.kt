package com.hdb.tourfolio.data.portfolio

import com.hdb.tourfolio.data.portfolio.mapper.toDomain
import com.hdb.tourfolio.data.portfolio.remote.PortfolioApiService
import com.hdb.tourfolio.domain.portfolio.model.Portfolio
import com.hdb.tourfolio.domain.portfolio.model.PortfolioSummary
import com.hdb.tourfolio.domain.portfolio.repository.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioRepositoryImpl
    @Inject
    constructor(
        private val portfolioApiService: PortfolioApiService,
    ) : PortfolioRepository {
        private val mutex = Mutex()
        private val portfolioCache = MutableStateFlow<Portfolio?>(null)

        override fun observePortfolio() = portfolioCache.asStateFlow()

        override suspend fun refreshPortfolio(sort: String): Portfolio =
            mutex.withLock {
                val portfolio = portfolioApiService.getPortfolio(sort).toDomain()
                portfolioCache.value = portfolio
                portfolio
            }

        override suspend fun getPortfolioSummary(period: String): PortfolioSummary =
            portfolioApiService.getPortfolioSummary(period).toDomain()
    }
