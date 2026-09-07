package com.hdb.tourfolio.domain.stock.usecase

import com.hdb.tourfolio.domain.stock.model.RegionalIndex
import com.hdb.tourfolio.domain.stock.repository.StockRepository
import javax.inject.Inject

class GetRegionalIndexUseCase
    @Inject
    constructor(
        private val stockRepository: StockRepository,
    ) {
        suspend operator fun invoke(): List<RegionalIndex> = stockRepository.getRegionalIndex()
    }
