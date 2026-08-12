package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.StockDto
import javax.inject.Inject

class StockRepository
    @Inject
    constructor(
        private val stockApiService: StockApiService,
    ) {
        suspend fun getStocks(): List<StockDto> = stockApiService.getStocks()

        suspend fun getTopGainers(): List<StockDto> = stockApiService.getTopGainers()

        suspend fun getTopLosers(): List<StockDto> = stockApiService.getTopLosers()
    }
