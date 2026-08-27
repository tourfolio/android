package com.hdb.tourfolio.domain.stock.usecase

import com.hdb.tourfolio.domain.stock.model.Stock
import com.hdb.tourfolio.domain.stock.repository.StockRepository
import javax.inject.Inject

class GetTopGainersUseCase
    @Inject
    constructor(
        private val stockRepository: StockRepository,
    ) {
        suspend operator fun invoke(): List<Stock> = stockRepository.getTopGainers()
    }
