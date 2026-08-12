@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.presentation.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.core.network.dto.PortfolioDto
import com.hdb.tourfolio.core.network.dto.PortfolioItemDto
import com.hdb.tourfolio.core.network.dto.StockDto
import com.hdb.tourfolio.feature.trade.presentation.components.AssetChartCard
import com.hdb.tourfolio.feature.trade.presentation.components.AssetPeriod
import com.hdb.tourfolio.feature.trade.presentation.components.PriceChangeType
import com.hdb.tourfolio.feature.trade.presentation.components.RankedStockCard
import com.hdb.tourfolio.feature.trade.presentation.components.alignAssetHistory
import com.hdb.tourfolio.feature.trade.presentation.components.mockAssetHistory
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural20
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

private data class RankedStockItem(
    val id: Long,
    val title: String,
    val currentPrice: Long,
    val prevPrice: Long,
    val priceText: String,
    val changeText: String,
    val changeType: PriceChangeType,
)

@Composable
fun HomeTab(
    modifier: Modifier = Modifier,
    onStockClick: (Long, String, Long?, Long?) -> Unit = { _, _, _, _ -> },
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val rankedStocksUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val portfolioUiState by viewModel.portfolioUiState.collectAsStateWithLifecycle()

    HomeTabContent(
        modifier = modifier,
        rankedStocksUiState = rankedStocksUiState,
        portfolioUiState = portfolioUiState,
        onStockClick = onStockClick,
        onRetryRankedStocksClick = { viewModel.fetchRankedStocks() },
        onRetryPortfolioClick = { viewModel.fetchPortfolio() },
    )
}

@Composable
private fun HomeTabContent(
    rankedStocksUiState: RankedStocksUiState,
    portfolioUiState: PortfolioUiState,
    onStockClick: (Long, String, Long?, Long?) -> Unit,
    onRetryRankedStocksClick: () -> Unit,
    onRetryPortfolioClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedPeriod by rememberSaveable {
        mutableStateOf(AssetPeriod.WEEK)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
    ) {
        when (portfolioUiState) {
            is PortfolioUiState.Loading -> {
                PortfolioLoading()
            }

            is PortfolioUiState.Error -> {
                PortfolioError(
                    message = portfolioUiState.message,
                    onRetryClick = onRetryPortfolioClick,
                )
            }

            is PortfolioUiState.Success -> {
                val portfolio = portfolioUiState.portfolio
                val totalAmount = portfolio.totalAssetValue
                val changeRate = portfolio.totalProfitLossRate
                val changeAmount = portfolio.toChangeAmount()

                /*
                 * 포트폴리오 API는 현재 스냅샷만 제공해 기간별 추이 데이터가 없다.
                 * 실제 총 평가금액과 그래프 끝점이 어긋나지 않도록, 임시 추이 데이터의
                 * 끝점을 실제 총 평가금액에 맞춰 보정해서 사용한다.
                 */
                val assetHistory =
                    remember(selectedPeriod, totalAmount) {
                        alignAssetHistory(mockAssetHistory(selectedPeriod), totalAmount)
                    }

                AssetChartCard(
                    label = "총 평가금액",
                    totalAmount = totalAmount,
                    changeAmount = changeAmount,
                    changeRate = changeRate,
                    assetHistory = assetHistory,
                    selectedPeriod = selectedPeriod,
                    onPeriodSelected = { period ->
                        selectedPeriod = period
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        when (rankedStocksUiState) {
            is RankedStocksUiState.Loading -> {
                RankedStocksLoading()
            }

            is RankedStocksUiState.Error -> {
                RankedStocksError(
                    message = rankedStocksUiState.message,
                    onRetryClick = onRetryRankedStocksClick,
                )
            }

            is RankedStocksUiState.Success -> {
                RankedStockSection(
                    title = "급등 TOP3",
                    items = rankedStocksUiState.topGainers.take(3).map { it.toRankedStockItem() },
                    onStockClick = onStockClick,
                )

                Spacer(modifier = Modifier.height(28.dp))

                RankedStockSection(
                    title = "급락 TOP3",
                    items = rankedStocksUiState.topLosers.take(3).map { it.toRankedStockItem() },
                    onStockClick = onStockClick,
                )
            }
        }
    }
}

@Composable
private fun PortfolioLoading(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun PortfolioError(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(320.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "총 평가금액을 불러오지 못했습니다.",
                style = LocalAppTypography.current.bodyLarge.bold,
                color = Natural20,
            )

            Text(
                text = message,
                style = LocalAppTypography.current.bodySmall.medium,
                color = Natural50,
            )

            TextButton(onClick = onRetryClick) {
                Text(
                    text = "다시 시도",
                    style = LocalAppTypography.current.bodySmall.bold,
                    color = Primary,
                )
            }
        }
    }
}

private fun PortfolioDto.toChangeAmount(): Long {
    val rateFraction = totalProfitLossRate / 100.0
    if (rateFraction <= -1.0) return 0L

    val costBasis = totalAssetValue / (1 + rateFraction)
    return (totalAssetValue - costBasis).toLong()
}

private fun StockDto.toRankedStockItem(): RankedStockItem {
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
        currentPrice = currentPrice,
        prevPrice = prevPrice,
        priceText = "%,dP".format(currentPrice),
        changeText = "%+,dP (%+.2f%%)".format(changeAmount, changeRate),
        changeType = changeType,
    )
}

@Composable
private fun RankedStocksLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().height(120.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun RankedStocksError(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "급등/급락 종목을 불러오지 못했습니다.",
            style = LocalAppTypography.current.bodyLarge.bold,
            color = Natural20,
        )

        Text(
            text = message,
            style = LocalAppTypography.current.bodySmall.medium,
            color = Natural50,
        )

        TextButton(onClick = onRetryClick) {
            Text(
                text = "다시 시도",
                style = LocalAppTypography.current.bodySmall.bold,
                color = Primary,
            )
        }
    }
}

@Composable
private fun RankedStockSection(
    title: String,
    items: List<RankedStockItem>,
    onStockClick: (Long, String, Long?, Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural20,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items.forEach { item ->
                RankedStockCard(
                    title = item.title,
                    priceText = item.priceText,
                    changeText = item.changeText,
                    changeType = item.changeType,
                    onClick = {
                        onStockClick(item.id, item.title, item.currentPrice, item.prevPrice)
                    },
                )
            }
        }
    }
}

private fun mockRankedStockDtos(rising: Boolean): List<StockDto> =
    if (rising) {
        listOf(
            StockDto(1L, "성산일출봉", "제주", 1, 15_200L, 13_350L, 13.85, "2026-07-30T21:56:41.981Z"),
            StockDto(2L, "해운대", "부산", 1, 9_800L, 8_880L, 10.36, "2026-07-30T21:56:41.981Z"),
            StockDto(3L, "불국사", "경북", 1, 21_400L, 19_750L, 8.35, "2026-07-30T21:56:41.981Z"),
        )
    } else {
        listOf(
            StockDto(4L, "남산타워", "서울", 1, 7_300L, 7_940L, -8.06, "2026-07-30T21:56:41.981Z"),
            StockDto(5L, "경복궁", "서울", 1, 18_900L, 20_020L, -5.59, "2026-07-30T21:56:41.981Z"),
            StockDto(6L, "안압지", "경북", 1, 12_050L, 12_630L, -4.59, "2026-07-30T21:56:41.981Z"),
        )
    }

private fun mockPortfolioDto(): PortfolioDto =
    PortfolioDto(
        memberId = 1L,
        username = "투어폴리오유저",
        cashBalance = 2_000_000L,
        totalStockValue = 8_500_000L,
        totalAssetValue = 10_500_000L,
        totalProfitLossRate = 5.32,
        items =
            listOf(
                PortfolioItemDto(
                    spotId = 1L,
                    spotName = "안압지",
                    quantity = 42,
                    averagePurchasePrice = 8_000L,
                    currentPrice = 9_200L,
                    evaluationAmount = 386_400L,
                    profitLossRate = 15.0,
                ),
            ),
    )

@Preview(
    name = "Home Tab Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 500,
)
@Composable
private fun HomeTabPreview() {
    TourfolioTheme(dynamicColor = false) {
        HomeTabContent(
            rankedStocksUiState =
                RankedStocksUiState.Success(
                    topGainers = mockRankedStockDtos(rising = true),
                    topLosers = mockRankedStockDtos(rising = false),
                ),
            portfolioUiState = PortfolioUiState.Success(mockPortfolioDto()),
            onStockClick = { _, _, _, _ -> },
            onRetryRankedStocksClick = {},
            onRetryPortfolioClick = {},
        )
    }
}
