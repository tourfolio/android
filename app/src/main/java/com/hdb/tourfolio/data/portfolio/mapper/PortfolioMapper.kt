package com.hdb.tourfolio.data.portfolio.mapper

import com.hdb.tourfolio.data.portfolio.remote.dto.PortfolioAssetHistoryPointDto
import com.hdb.tourfolio.data.portfolio.remote.dto.PortfolioDto
import com.hdb.tourfolio.data.portfolio.remote.dto.PortfolioItemDto
import com.hdb.tourfolio.data.portfolio.remote.dto.PortfolioSummaryDto
import com.hdb.tourfolio.domain.portfolio.model.Portfolio
import com.hdb.tourfolio.domain.portfolio.model.PortfolioAssetHistoryPoint
import com.hdb.tourfolio.domain.portfolio.model.PortfolioItem
import com.hdb.tourfolio.domain.portfolio.model.PortfolioSummary

fun PortfolioDto.toDomain(): Portfolio =
    Portfolio(
        memberId = memberId,
        username = username,
        cashBalance = cashBalance,
        totalStockValue = totalStockValue,
        totalAssetValue = totalAssetValue,
        totalProfitLossRate = totalProfitLossRate,
        items = items.map { it.toDomain() },
    )

fun PortfolioItemDto.toDomain(): PortfolioItem =
    PortfolioItem(
        spotId = spotId,
        spotName = spotName,
        quantity = quantity,
        averagePurchasePrice = averagePurchasePrice,
        currentPrice = currentPrice,
        evaluationAmount = evaluationAmount,
        profitLossRate = profitLossRate,
    )

fun PortfolioSummaryDto.toDomain(): PortfolioSummary =
    PortfolioSummary(
        totalAsset = totalAsset,
        totalEvaluation = totalEvaluation,
        totalPurchase = totalPurchase,
        totalProfitLoss = totalProfitLoss,
        profitRate = profitRate,
        cashBalance = cashBalance,
        assetHistory = assetHistory.map { it.toDomain() },
    )

fun PortfolioAssetHistoryPointDto.toDomain(): PortfolioAssetHistoryPoint =
    PortfolioAssetHistoryPoint(
        date = date,
        totalAsset = totalAsset,
    )
