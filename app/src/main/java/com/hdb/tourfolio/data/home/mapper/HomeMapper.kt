package com.hdb.tourfolio.data.home.mapper

import com.hdb.tourfolio.data.home.remote.dto.HomeCardCollectionDto
import com.hdb.tourfolio.data.home.remote.dto.HomePortfolioDto
import com.hdb.tourfolio.data.home.remote.dto.HomeRecommendedSpotDto
import com.hdb.tourfolio.data.home.remote.dto.HomeResponseDto
import com.hdb.tourfolio.domain.home.model.Home
import com.hdb.tourfolio.domain.home.model.HomeCardCollection
import com.hdb.tourfolio.domain.home.model.HomePortfolio
import com.hdb.tourfolio.domain.home.model.HomeRecommendedSpot

fun HomeResponseDto.toDomain(): Home =
    Home(
        portfolio =
            portfolio.toDomain(),
        cardCollection =
            cardCollection.toDomain(),
        recommendedSpots =
            recommendedSpots.map { spot ->
                spot.toDomain()
            },
    )

private fun HomePortfolioDto.toDomain(): HomePortfolio =
    HomePortfolio(
        totalAsset = totalAsset,
        todayProfit = todayProfit,
        todayProfitRate = todayProfitRate,
        totalProfitRate = totalProfitRate,
        stockCount = stockCount,
    )

private fun HomeCardCollectionDto.toDomain(): HomeCardCollection =
    HomeCardCollection(
        ownedCount = ownedCount,
        totalCount = totalCount,
        collectionRate = collectionRate,
    )

private fun HomeRecommendedSpotDto.toDomain(): HomeRecommendedSpot =
    HomeRecommendedSpot(
        id = spotId,
        title = name,
        imageUrl = imageUrl,
        description = description,
        tags = tags,
    )
