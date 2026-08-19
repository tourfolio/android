package com.hdb.tourfolio.domain.stock.usecase

import com.hdb.tourfolio.domain.stock.model.StockChartPoint
import com.hdb.tourfolio.domain.stock.repository.StockRepository
import javax.inject.Inject

class GetStockChartUseCase
    @Inject
    constructor(
        private val stockRepository: StockRepository,
    ) {
        suspend operator fun invoke(
            spotId: Long,
            period: String = "1W",
        ): List<StockChartPoint> = stockRepository.getStockChart(spotId = spotId, period = period)
    }
