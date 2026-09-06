package com.hdb.tourfolio.data.stock.mapper

import com.hdb.tourfolio.data.stock.remote.dto.RegionalIndexDto
import com.hdb.tourfolio.data.stock.remote.dto.StockChartPointDto
import com.hdb.tourfolio.data.stock.remote.dto.StockDto
import com.hdb.tourfolio.domain.stock.model.RegionalIndex
import com.hdb.tourfolio.domain.stock.model.Stock
import com.hdb.tourfolio.domain.stock.model.StockChartPoint

fun StockDto.toDomain(): Stock =
    Stock(
        id = id,
        name = name,
        areaCode = areaCode,
        tier = tier,
        currentPrice = currentPrice,
        prevPrice = prevPrice,
        changeRate = changeRate,
        lastUpdated = lastUpdated,
        regionName = regionName,
        address = address,
        todayTradeVolume = todayTradeVolume,
        visitorForecast = visitorForecast,
        demandIntensity = demandIntensity,
        resourceDemand = resourceDemand,
    )

fun StockChartPointDto.toDomain(): StockChartPoint =
    StockChartPoint(
        date = date,
        price = price,
    )

fun RegionalIndexDto.toDomain(): RegionalIndex =
    RegionalIndex(
        region = region,
        averageChangeRate = averageChangeRate,
        spotCount = spotCount,
    )
