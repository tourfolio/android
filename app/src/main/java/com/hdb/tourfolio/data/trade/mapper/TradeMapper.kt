package com.hdb.tourfolio.data.trade.mapper

import com.hdb.tourfolio.data.trade.remote.dto.TradeResponseDto
import com.hdb.tourfolio.domain.trade.model.TradeResult
import com.hdb.tourfolio.domain.trade.model.TradeType

fun TradeType.toApiValue(): String =
    when (this) {
        TradeType.BUY -> "BUY"
        TradeType.SELL -> "SELL"
    }

fun TradeResponseDto.toDomain(): TradeResult =
    TradeResult(
        id = id,
        spotId = spotId,
        type = TradeType.valueOf(type),
        quantity = quantity,
        price = price,
        totalAmount = totalAmount,
        executedAt = executedAt,
        realizedProfit = realizedProfit,
    )
