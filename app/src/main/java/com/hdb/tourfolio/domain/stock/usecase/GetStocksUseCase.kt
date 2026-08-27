package com.hdb.tourfolio.domain.stock.usecase

import com.hdb.tourfolio.domain.stock.model.Stock
import com.hdb.tourfolio.domain.stock.repository.StockRepository
import javax.inject.Inject

class GetStocksUseCase
    @Inject
    constructor(
        private val stockRepository: StockRepository,
    ) {
        suspend operator fun invoke(
            region: String = "ALL",
            keyword: String? = null,
            tags: List<String>? = null,
            sortBy: String = "changeRate",
            sortOrder: String = "DESC",
        ): List<Stock> =
            stockRepository.getStocks(
                region = region,
                keyword = keyword,
                tags = tags,
                sortBy = sortBy,
                sortOrder = sortOrder,
            )
    }
