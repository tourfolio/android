package com.hdb.tourfolio.feature.trade.presentation.tabs

import com.hdb.tourfolio.domain.stock.model.Stock
import com.hdb.tourfolio.feature.trade.presentation.components.PriceChangeType

internal data class RankedStockItem(
    val id: Long,
    val title: String,
    val regionName: String,
    val currentPrice: Long,
    val prevPrice: Long,
    val priceText: String,
    val changeText: String,
    val changeType: PriceChangeType,
)

internal fun Stock.toRankedStockItem(): RankedStockItem {
    val changeAmount = currentPrice - prevPrice
    val changeType =
        when {
            changeRate > 0 -> PriceChangeType.RISE
            changeRate < 0 -> PriceChangeType.FALL
            else -> PriceChangeType.UNCHANGED
        }

    return RankedStockItem(
        id = id,
        title = name,
        regionName = regionName,
        currentPrice = currentPrice,
        prevPrice = prevPrice,
        priceText = "%,dP".format(currentPrice),
        changeText = "%+,dP (%+.2f%%)".format(changeAmount, changeRate),
        changeType = changeType,
    )
}
