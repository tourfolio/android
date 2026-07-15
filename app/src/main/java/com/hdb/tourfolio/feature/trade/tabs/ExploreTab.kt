@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.feature.trade.components.FilterDropdown
import com.hdb.tourfolio.feature.trade.components.PriceChangeType
import com.hdb.tourfolio.feature.trade.components.RegionFilterSidebar
import com.hdb.tourfolio.feature.trade.components.TourStockCard
import com.hdb.tourfolio.feature.trade.components.TradeCategoryChip
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.TourfolioTheme

private data class ExploreStockItem(
    val id: Long,
    val title: String,
    val region: String,
    val category: String,
    val priceText: String,
    val changeText: String,
    val changeType: PriceChangeType,
)

@Composable
fun ExploreTab(modifier: Modifier = Modifier) {
    var selectedRegion by rememberSaveable {
        mutableStateOf("전체")
    }

    var selectedCategory by rememberSaveable {
        mutableStateOf("전체")
    }

    var selectedSortOption by rememberSaveable {
        mutableStateOf("등락률")
    }

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

    val sortOptions =
        remember {
            listOf(
                "등락률",
                "거래량",
                "가격순",
            )
        }

    /*
     * 임시 데이터
     */
    val stockItems =
        remember {
            listOf(
                ExploreStockItem(
                    id = 1L,
                    title = "경복궁",
                    region = "서울",
                    category = "역사",
                    priceText = "00,000P",
                    changeText = "+00,000P (+0.00%)",
                    changeType = PriceChangeType.RISE,
                ),
                ExploreStockItem(
                    id = 2L,
                    title = "해운대",
                    region = "부산",
                    category = "자연",
                    priceText = "00,000P",
                    changeText = "+00,000P (+0.00%)",
                    changeType = PriceChangeType.RISE,
                ),
                ExploreStockItem(
                    id = 3L,
                    title = "안압지",
                    region = "경북",
                    category = "역사",
                    priceText = "00,000P",
                    changeText = "+00,000P (+0.00%)",
                    changeType = PriceChangeType.RISE,
                ),
                ExploreStockItem(
                    id = 4L,
                    title = "해동용궁사",
                    region = "부산",
                    category = "역사",
                    priceText = "00,000P",
                    changeText = "+00,000P (+0.00%)",
                    changeType = PriceChangeType.RISE,
                ),
                ExploreStockItem(
                    id = 5L,
                    title = "함안해변",
                    region = "경남",
                    category = "자연",
                    priceText = "00,000P",
                    changeText = "-00,000P (-0.00%)",
                    changeType = PriceChangeType.FALL,
                ),
                ExploreStockItem(
                    id = 6L,
                    title = "남산타워",
                    region = "서울",
                    category = "문화",
                    priceText = "00,000P",
                    changeText = "-00,000P (-0.00%)",
                    changeType = PriceChangeType.FALL,
                ),
                ExploreStockItem(
                    id = 7L,
                    title = "불국사",
                    region = "경북",
                    category = "역사",
                    priceText = "00,000P",
                    changeText = "-00,000P (-0.00%)",
                    changeType = PriceChangeType.FALL,
                ),
                ExploreStockItem(
                    id = 8L,
                    title = "성산일출봉",
                    region = "제주",
                    category = "자연",
                    priceText = "00,000P",
                    changeText = "+00,000P (+0.00%)",
                    changeType = PriceChangeType.RISE,
                ),
            )
        }

    val filteredStockItems =
        stockItems.filter { item ->
            val matchesRegion =
                selectedRegion == "전체" ||
                    item.region == selectedRegion

            val matchesCategory =
                selectedCategory == "전체" ||
                    item.category == selectedCategory

            matchesRegion && matchesCategory
        }

    Row(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
    ) {
        RegionFilterSidebar(
            regions = regions,
            selectedRegion = selectedRegion,
            onRegionSelected = { region ->
                selectedRegion = region
                // ViewModel을 사용할 때는 지역 선택 이벤트 전달
            },
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
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    // ViewModel을 사용할 때는 유형 선택 이벤트 전달
                },
            )

            StockListHeader(
                totalCount = filteredStockItems.size,
                selectedSortOption = selectedSortOption,
                sortOptions = sortOptions,
                onSortOptionSelected = { option ->
                    selectedSortOption = option
                    /*
                     * 현재는 선택 문구만 변경
                     */
                },
            )

            if (filteredStockItems.isEmpty()) {
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
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            bottom = 24.dp,
                        ),
                ) {
                    items(
                        items = filteredStockItems,
                        key = { item -> item.id },
                    ) { item ->
                        TourStockCard(
                            title = item.title,
                            priceText = item.priceText,
                            changeText = item.changeText,
                            changeType = item.changeType,
                            onClick = {
                                // 상세화면 없다면 onClick 이벤트 제거
                            },
                        )
                    }
                }
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
            androidx.compose.foundation.layout.PaddingValues(
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
    selectedSortOption: String,
    sortOptions: List<String>,
    onSortOptionSelected: (String) -> Unit,
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
            style = LocalAppTypography.current.bodyLarge.bold,
            color = Natural10,
        )

        FilterDropdown(
            selectedOption = selectedSortOption,
            options = sortOptions,
            onOptionSelected = onSortOptionSelected,
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
            color = com.hdb.tourfolio.ui.theme.Natural60,
        )
    }
}

@Preview(
    name = "Explore Tab Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 760,
)
@Composable
private fun ExploreTabPreview() {
    TourfolioTheme(
        dynamicColor = false,
    ) {
        ExploreTab()
    }
}
