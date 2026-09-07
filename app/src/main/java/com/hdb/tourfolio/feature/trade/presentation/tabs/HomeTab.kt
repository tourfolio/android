@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.presentation.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.domain.portfolio.model.PortfolioAssetHistoryPoint
import com.hdb.tourfolio.domain.portfolio.model.PortfolioSummary
import com.hdb.tourfolio.domain.stock.model.RegionalIndex
import com.hdb.tourfolio.domain.stock.model.Stock
import com.hdb.tourfolio.feature.trade.presentation.components.AssetChartCard
import com.hdb.tourfolio.feature.trade.presentation.components.AssetPoint
import com.hdb.tourfolio.feature.trade.presentation.components.PeriodTabRow
import com.hdb.tourfolio.feature.trade.presentation.components.RankedStockCard
import com.hdb.tourfolio.ui.theme.Blue
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural20
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural99
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Red
import com.hdb.tourfolio.ui.theme.TourfolioTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeTab(
    modifier: Modifier = Modifier,
    onStockClick: (Long, String, Long?, Long?) -> Unit = { _, _, _, _ -> },
    viewModel: TradeHomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeTabContent(
        modifier = modifier,
        isInitialLoading = state.isInitialLoading,
        isRefreshing = state.isRefreshing,
        rankedStocksUiState = state.rankedStocks,
        portfolioSummaryUiState = state.portfolioSummary,
        regionalIndexUiState = state.regionalIndex,
        selectedPeriod = state.selectedPeriod,
        onStockClick = onStockClick,
        onRefresh = { viewModel.processIntent(TradeHomeIntent.Refresh) },
        onRetryRankedStocksClick = { viewModel.processIntent(TradeHomeIntent.RetryRankedStocks) },
        onRetryRegionalIndexClick = { viewModel.processIntent(TradeHomeIntent.RetryRegionalIndex) },
        onPeriodSelected = { period -> viewModel.processIntent(TradeHomeIntent.SelectPeriod(period)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTabContent(
    isInitialLoading: Boolean,
    isRefreshing: Boolean,
    rankedStocksUiState: RankedStocksUiState,
    portfolioSummaryUiState: PortfolioSummaryUiState,
    regionalIndexUiState: RegionalIndexUiState,
    selectedPeriod: PortfolioPeriod,
    onStockClick: (Long, String, Long?, Long?) -> Unit,
    onRefresh: () -> Unit,
    onRetryRankedStocksClick: () -> Unit,
    onRetryRegionalIndexClick: () -> Unit,
    onPeriodSelected: (PortfolioPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isInitialLoading) {
        /*
         * 급등/급락/포트폴리오 요약 API를 병렬로 불러오는 동안에는
         * 섹션별 스피너 대신 화면 중앙에 로딩 하나만 보여준다.
         */
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
        ) {
            when (portfolioSummaryUiState) {
                is PortfolioSummaryUiState.Loading -> {
                    PortfolioLoading()
                }

                is PortfolioSummaryUiState.Error -> {
                    PortfolioError(
                        message = portfolioSummaryUiState.message,
                        periods = PortfolioPeriod.entries,
                        selectedPeriod = selectedPeriod,
                        onPeriodSelected = onPeriodSelected,
                        onRetryClick = { onPeriodSelected(selectedPeriod) },
                    )
                }

                is PortfolioSummaryUiState.Success -> {
                    val summary = portfolioSummaryUiState.summary
                    val assetHistory =
                        remember(summary.assetHistory) {
                            summary.assetHistory.map { it.toAssetPoint() }
                        }

                    AssetChartCard(
                        label = "총 평가금액",
                        totalAmount = summary.totalAsset,
                        changeAmount = summary.totalProfitLoss,
                        changeRate = summary.profitRate,
                        assetHistory = assetHistory,
                        periods = PortfolioPeriod.entries,
                        selectedPeriod = selectedPeriod,
                        onPeriodSelected = onPeriodSelected,
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            when (regionalIndexUiState) {
                is RegionalIndexUiState.Loading -> {
                    RegionalIndexLoading()
                }

                is RegionalIndexUiState.Error -> {
                    RegionalIndexError(
                        message = regionalIndexUiState.message,
                        onRetryClick = onRetryRegionalIndexClick,
                    )
                }

                is RegionalIndexUiState.Success -> {
                    RegionalIndexSection(
                        items = regionalIndexUiState.items,
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
}

private fun PortfolioAssetHistoryPoint.toAssetPoint(): AssetPoint {
    val label = runCatching { LocalDate.parse(date).format(DateTimeFormatter.ofPattern("M/d")) }.getOrDefault(date)
    return AssetPoint(dateLabel = label, value = totalAsset)
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
    periods: List<PortfolioPeriod>,
    selectedPeriod: PortfolioPeriod,
    onPeriodSelected: (PortfolioPeriod) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Natural99)
                .padding(20.dp),
    ) {
        Text(
            text = "총 평가금액",
            style = LocalAppTypography.current.bodySmall.medium,
            color = Natural10,
            maxLines = 1,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(260.dp),
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

        PeriodTabRow(
            periods = periods,
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun RegionalIndexLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().height(100.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun RegionalIndexError(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "오늘의 주요 지수를 불러오지 못했습니다.",
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
private fun RegionalIndexSection(
    items: List<RegionalIndex>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = "오늘의 주요 지수",
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural20,
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items = items, key = { it.region }) { item ->
                RegionalIndexTile(item = item)
            }
        }
    }
}

@Composable
private fun RegionalIndexTile(
    item: RegionalIndex,
    modifier: Modifier = Modifier,
) {
    val changeColor =
        when {
            item.averageChangeRate > 0 -> Red
            item.averageChangeRate < 0 -> Blue
            else -> Natural50
        }

    Column(
        modifier =
            modifier
                .width(84.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Natural99)
                .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Text(
            text = item.region,
            style = LocalAppTypography.current.bodySmall.bold,
            color = Natural10,
            maxLines = 1,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "%+.2f%%".format(item.averageChangeRate),
            style = LocalAppTypography.current.labelLarge.bold,
            color = changeColor,
            maxLines = 1,
        )
    }
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
                    regionName = item.regionName,
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

private fun mockRankedStocks(rising: Boolean): List<Stock> =
    if (rising) {
        listOf(
            Stock(1L, "성산일출봉", "제주", 1, 15_200L, 13_350L, 13.85, "2026-07-30T21:56:41.981Z", "제주", "제주 서귀포시 성산읍", 100L, 0.5, 0.5, 0.5),
            Stock(2L, "해운대", "부산", 1, 9_800L, 8_880L, 10.36, "2026-07-30T21:56:41.981Z", "부산", "부산 해운대구", 100L, 0.5, 0.5, 0.5),
            Stock(3L, "불국사", "경북", 1, 21_400L, 19_750L, 8.35, "2026-07-30T21:56:41.981Z", "경북", "경북 경주시 진현동", 100L, 0.5, 0.5, 0.5),
        )
    } else {
        listOf(
            Stock(4L, "남산타워", "서울", 1, 7_300L, 7_940L, -8.06, "2026-07-30T21:56:41.981Z", "서울", "서울 용산구", 100L, 0.5, 0.5, 0.5),
            Stock(5L, "경복궁", "서울", 1, 18_900L, 20_020L, -5.59, "2026-07-30T21:56:41.981Z", "서울", "서울 종로구", 100L, 0.5, 0.5, 0.5),
            Stock(6L, "안압지", "경북", 1, 12_050L, 12_630L, -4.59, "2026-07-30T21:56:41.981Z", "경북", "경북 경주시", 100L, 0.5, 0.5, 0.5),
        )
    }

private fun mockPortfolioSummary(): PortfolioSummary =
    PortfolioSummary(
        totalAsset = 10_500_000L,
        totalEvaluation = 8_500_000L,
        totalPurchase = 8_000_000L,
        totalProfitLoss = 500_000L,
        profitRate = 5.32,
        cashBalance = 2_000_000L,
        assetHistory =
            listOf(
                PortfolioAssetHistoryPoint(date = "2026-07-24", totalAsset = 9_800_000L),
                PortfolioAssetHistoryPoint(date = "2026-07-27", totalAsset = 10_100_000L),
                PortfolioAssetHistoryPoint(date = "2026-07-30", totalAsset = 10_500_000L),
            ),
    )

private fun mockRegionalIndex(): List<RegionalIndex> =
    listOf(
        RegionalIndex(region = "서울", averageChangeRate = 1.24, spotCount = 12),
        RegionalIndex(region = "부산", averageChangeRate = -0.85, spotCount = 8),
        RegionalIndex(region = "경주", averageChangeRate = 0.42, spotCount = 5),
        RegionalIndex(region = "제주", averageChangeRate = -1.10, spotCount = 6),
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
            isInitialLoading = false,
            isRefreshing = false,
            rankedStocksUiState =
                RankedStocksUiState.Success(
                    topGainers = mockRankedStocks(rising = true),
                    topLosers = mockRankedStocks(rising = false),
                ),
            portfolioSummaryUiState = PortfolioSummaryUiState.Success(mockPortfolioSummary()),
            regionalIndexUiState = RegionalIndexUiState.Success(mockRegionalIndex()),
            selectedPeriod = PortfolioPeriod.WEEK,
            onStockClick = { _, _, _, _ -> },
            onRefresh = {},
            onRetryRankedStocksClick = {},
            onRetryRegionalIndexClick = {},
            onPeriodSelected = {},
        )
    }
}
