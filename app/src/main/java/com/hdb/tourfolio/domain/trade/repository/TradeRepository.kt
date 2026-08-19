package com.hdb.tourfolio.domain.trade.repository

import com.hdb.tourfolio.domain.trade.model.TradeResult
import com.hdb.tourfolio.domain.trade.model.TradeType

interface TradeRepository {
    suspend fun trade(
        spotId: Long,
        type: TradeType,
        quantity: Int,
    ): TradeResult
}
