@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.presentation.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.domain.stock.model.Stock
import com.hdb.tourfolio.feature.trade.presentation.components.FilterDropdown
import com.hdb.tourfolio.feature.trade.presentation.components.RankedStockCard
import com.hdb.tourfolio.feature.trade.presentation.components.RegionFilterSidebar
import com.hdb.tourfolio.feature.trade.presentation.components.TradeCategoryChip
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun TradeExploreTab(
    modifier: Modifier = Modifier,
    onStockClick: (Long, String, Long?, Long?) -> Unit = { _, _, _, _ -> },
    viewModel: TradeExploreViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TradeExploreTabContent(
        modifier = modifier,
        state = state,
        onStockClick = onStockClick,
        onRetryClick = { viewModel.processIntent(TradeExploreIntent.FetchStocks) },
        onRegionSelected = { region -> viewModel.processIntent(TradeExploreIntent.SelectRegion(region)) },
        onCategorySelected = { category -> viewModel.processIntent(TradeExploreIntent.SelectCategory(category)) },
        onSortOptionSelected = { option -> viewModel.processIntent(TradeExploreIntent.SelectSortOption(option)) },
    )
}

@Composable
private fun TradeExploreTabContent(
    state: TradeExploreState,
    onStockClick: (Long, String, Long?, Long?) -> Unit,
    onRetryClick: () -> Unit,
    onRegionSelected: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onSortOptionSelected: (TradeExploreSortOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    val regions =
        remember {
            listOf(
                "전체",
                "서울",
                "부산",
                "대구",
                "인천",
                "광주",
                "대전",
                "울산",
                "경기",
                "강원",
                "충북",
                "충남",
                "전북",
                "전남",
                "경북",
                "경남",
                "제주",
            )
        }

    val categories =
        remember {
            listOf(
                "전체",
                "역사",
                "자연",
                "문화",
            )
        }

    val sortOptions = remember { TradeExploreSortOption.entries }

    val stockItems = state.filteredStocks.map { it.toRankedStockItem() }

    Row(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
    ) {
        RegionFilterSidebar(
            regions = regions,
            selectedRegion = state.selectedRegion,
            onRegionSelected = onRegionSelected,
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(
                        start = 8.dp,
                        end = 16.dp,
                        top = 8.dp,
                    ),
        ) {
            CategoryFilterRow(
                categories = categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = onCategorySelected,
            )

            when (val stocksResult = state.stocksResult) {
                is ExploreStocksUiState.Loading -> {
                    LoadingStockResult(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                    )
                }

                is ExploreStocksUiState.Error -> {
                    ErrorStockResult(
                        message = stocksResult.message,
                        onRetryClick = onRetryClick,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                    )
                }

                is ExploreStocksUiState.Success -> {
                    StockListHeader(
                        totalCount = stockItems.size,
                        selectedSortOption = state.selectedSortOption,
                        sortOptions = sortOptions,
                        onSortOptionSelected = onSortOptionSelected,
                    )

                    if (stockItems.isEmpty()) {
                        EmptyStockResult(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 24.dp),
                        ) {
                            items(
                                items = stockItems,
                                key = { item -> item.id },
                            ) { item ->
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
            }
        }
    }
}

@Composable
private fun LoadingStockResult(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun ErrorStockResult(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "관광지 종목을 불러오지 못했습니다.",
                style = LocalAppTypography.current.bodyLarge.bold,
                color = Natural10,
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

@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding =
            PaddingValues(
                end = 8.dp,
            ),
    ) {
        items(categories) { category ->
            TradeCategoryChip(
                label = category,
                selected = selectedCategory == category,
                onClick = {
                    onCategorySelected(category)
                },
            )
        }
    }
}

@Composable
private fun StockListHeader(
    totalCount: Int,
    selectedSortOption: TradeExploreSortOption,
    sortOptions: List<TradeExploreSortOption>,
    onSortOptionSelected: (TradeExploreSortOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    top = 2.dp,
                    bottom = 2.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "총 ${totalCount}개",
            style = LocalAppTypography.current.bodySmall.bold,
            color = Natural10,
        )

        FilterDropdown(
            selectedOption = selectedSortOption.label,
            options = sortOptions.map { it.label },
            onOptionSelected = { label ->
                sortOptions.firstOrNull { it.label == label }?.let(onSortOptionSelected)
            },
        )
    }
}

@Composable
private fun EmptyStockResult(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "조건에 맞는 관광지가 없습니다.",
            style = LocalAppTypography.current.bodyLarge.medium,
            color = Natural60,
        )
    }
}

private fun mockExploreStocks(): List<Stock> =
    listOf(
        Stock(1L, "경복궁", "서울", 1, 18_900L, 17_780L, 5.59, "2026-07-30T21:56:41.981Z", "서울", "서울 종로구", 100L, 0.5, 0.5, 0.5),
        Stock(2L, "해운대", "부산", 1, 9_800L, 8_880L, 10.36, "2026-07-30T21:56:41.981Z", "부산", "부산 해운대구", 100L, 0.5, 0.5, 0.5),
        Stock(3L, "안압지", "경북", 1, 12_050L, 12_630L, -4.59, "2026-07-30T21:56:41.981Z", "경북", "경북 경주시", 100L, 0.5, 0.5, 0.5),
        Stock(4L, "해동용궁사", "부산", 1, 14_200L, 13_400L, 5.97, "2026-07-30T21:56:41.981Z", "부산", "부산 기장군", 100L, 0.5, 0.5, 0.5),
        Stock(5L, "함안해변", "경남", 1, 6_400L, 6_900L, -7.25, "2026-07-30T21:56:41.981Z", "경남", "경남 함안군", 100L, 0.5, 0.5, 0.5),
        Stock(6L, "남산타워", "서울", 1, 7_300L, 7_940L, -8.06, "2026-07-30T21:56:41.981Z", "서울", "서울 용산구", 100L, 0.5, 0.5, 0.5),
        Stock(7L, "불국사", "경북", 1, 21_400L, 19_750L, 8.35, "2026-07-30T21:56:41.981Z", "경북", "경북 경주시 진현동", 100L, 0.5, 0.5, 0.5),
        Stock(8L, "성산일출봉", "제주", 1, 15_200L, 13_350L, 13.85, "2026-07-30T21:56:41.981Z", "제주", "제주 서귀포시 성산읍", 100L, 0.5, 0.5, 0.5),
    )

@Preview(
    name = "Trade Explore Tab Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 760,
)
@Composable
private fun TradeExploreTabPreview() {
    TourfolioTheme(
        dynamicColor = false,
    ) {
        TradeExploreTabContent(
            state = TradeExploreState(stocksResult = ExploreStocksUiState.Success(mockExploreStocks())),
            onStockClick = { _, _, _, _ -> },
            onRetryClick = {},
            onRegionSelected = {},
            onCategorySelected = {},
            onSortOptionSelected = {},
        )
    }
}
