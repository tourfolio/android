package com.hdb.tourfolio.data.watchlist.mapper

import com.hdb.tourfolio.data.watchlist.remote.dto.WatchlistItemDto
import com.hdb.tourfolio.data.watchlist.remote.dto.WatchlistRegisterResponseDto
import com.hdb.tourfolio.domain.watchlist.model.WatchlistItem
import com.hdb.tourfolio.domain.watchlist.model.WatchlistRegistration

fun WatchlistItemDto.toDomain(): WatchlistItem =
    WatchlistItem(
        id = id,
        spotId = spotId,
        spotName = spotName,
        region = region,
        theme = theme,
        currentPrice = currentPrice,
        changeRate = changeRate,
        prevPrice = derivePrevPrice(currentPrice, changeRate),
    )

fun WatchlistRegisterResponseDto.toDomain(): WatchlistRegistration =
    WatchlistRegistration(
        id = id,
        spotId = spotId,
        createdAt = createdAt,
    )

/*
 * 응답에 어제 가격이 없어 현재가와 등락률로 역산한다.
 */
private fun derivePrevPrice(
    currentPrice: Long,
    changeRate: Double,
): Long {
    val rateFraction = changeRate / 100.0
    return if (rateFraction <= -1.0) {
        currentPrice
    } else {
        (currentPrice / (1 + rateFraction)).toLong()
    }
}
