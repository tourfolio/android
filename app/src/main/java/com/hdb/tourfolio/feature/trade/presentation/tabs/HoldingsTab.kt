@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.presentation.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.hdb.tourfolio.feature.trade.presentation.components.FilterDropdown
import com.hdb.tourfolio.feature.trade.presentation.components.PriceChangeType
import com.hdb.tourfolio.feature.trade.presentation.components.TourStockCard
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary99
import com.hdb.tourfolio.ui.theme.Red
import com.hdb.tourfolio.ui.theme.TourfolioTheme
import java.text.NumberFormat
import java.util.Locale

private data class HoldingStockItem(
    val id: Long,
    val title: String,
    val currentPrice: Long,
    val evaluationAmount: Long,
    val profitAmount: Long,
    val profitRate: Double,
    val quantity: Int,
)

@Composable
fun HoldingsTab(
    modifier: Modifier = Modifier,
    onStockClick: (Long, String, Long?, Long?) -> Unit = { _, _, _, _ -> },
    viewModel: HoldingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HoldingsTabContent(
        modifier = modifier,
        uiState = uiState,
        onStockClick = onStockClick,
        onRetryClick = { viewModel.fetchPortfolio() },
    )
}

@Composable
private fun HoldingsTabContent(
    uiState: PortfolioUiState,
    onStockClick: (Long, String, Long?, Long?) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is PortfolioUiState.Loading -> {
            LoadingHoldings(
                modifier =
                    modifier
                        .fillMaxSize()
                        .background(Natural100),
            )
        }

        is PortfolioUiState.Error -> {
            ErrorHoldings(
                message = uiState.message,
                onRetryClick = onRetryClick,
                modifier =
                    modifier
                        .fillMaxSize()
                        .background(Natural100),
            )
        }

        is PortfolioUiState.Success -> {
            HoldingsList(
                portfolio = uiState.portfolio,
                onStockClick = onStockClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun HoldingsList(
    portfolio: PortfolioDto,
    onStockClick: (Long, String, Long?, Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedSortOption by rememberSaveable {
        mutableStateOf("수익률")
    }

    val sortOptions =
        remember {
            listOf(
                "수익률",
                "평가금액",
                "보유수량",
            )
        }

    val holdingStocks =
        remember(portfolio) {
            portfolio.items.map { it.toHoldingStockItem() }
        }

    val sortedHoldingStocks =
        remember(
            holdingStocks,
            selectedSortOption,
        ) {
            when (selectedSortOption) {
                "평가금액" -> {
                    holdingStocks.sortedByDescending { item ->
                        item.evaluationAmount
                    }
                }

                "보유수량" -> {
                    holdingStocks.sortedByDescending { item ->
                        item.quantity
                    }
                }

                else -> {
                    holdingStocks.sortedByDescending { item ->
                        item.profitRate
                    }
                }
            }
        }

    val totalEvaluationAmount =
        holdingStocks.sumOf { item ->
            item.evaluationAmount
        }

    val totalProfit =
        holdingStocks.sumOf { item ->
            item.profitAmount
        }

    val principal =
        totalEvaluationAmount - totalProfit

    val totalProfitRate =
        if (principal != 0L) {
            totalProfit.toDouble() / principal.toDouble() * 100
        } else {
            0.0
        }

    /*
     * 임시 데이터 - 월간 수익 API 연동 전까지 사용
     */
    val monthlyProfit = 240_000L
    val monthlyProfitRate = 2.15
    val holdingPoint = portfolio.cashBalance

    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
        contentPadding =
            PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 24.dp,
                bottom = 32.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            HoldingsSummary(
                totalEvaluationAmount = totalEvaluationAmount,
                principal = principal,
                totalProfit = totalProfit,
                totalProfitRate = totalProfitRate,
                monthlyProfit = monthlyProfit,
                monthlyProfitRate = monthlyProfitRate,
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            HoldingPointCard(
                point = holdingPoint,
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            HoldingsListHeader(
                totalCount = sortedHoldingStocks.size,
                selectedSortOption = selectedSortOption,
                sortOptions = sortOptions,
                onSortOptionSelected = { option ->
                    selectedSortOption = option
                },
            )
        }

        if (sortedHoldingStocks.isEmpty()) {
            item {
                EmptyHoldings()
            }
        } else {
            items(
                items = sortedHoldingStocks,
                key = { item -> item.id },
            ) { item ->
                TourStockCard(
                    title = item.title,
                    priceText = "${formatNumber(item.evaluationAmount)}P",
                    changeText =
                        buildChangeText(
                            amount = item.profitAmount,
                            rate = item.profitRate,
                        ),
                    changeType = item.profitAmount.toPriceChangeType(),
                    showLike = false,
                    onClick = {
                        onStockClick(item.id, item.title, item.currentPrice, null)
                    },
                )
            }
        }
    }
}

@Composable
private fun LoadingHoldings(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun ErrorHoldings(
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
                text = "보유 종목을 불러오지 못했습니다.",
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
private fun EmptyHoldings(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "보유 중인 관광지가 없습니다.",
            style = LocalAppTypography.current.bodyLarge.medium,
            color = Natural60,
        )
    }
}

private fun PortfolioItemDto.toHoldingStockItem(): HoldingStockItem {
    val profitAmount = (currentPrice - averagePurchasePrice) * quantity

    return HoldingStockItem(
        id = spotId,
        title = spotName,
        currentPrice = currentPrice,
        evaluationAmount = evaluationAmount,
        profitAmount = profitAmount,
        profitRate = profitLossRate,
        quantity = quantity,
    )
}

@Composable
private fun HoldingsSummary(
    totalEvaluationAmount: Long,
    principal: Long,
    totalProfit: Long,
    totalProfitRate: Double,
    monthlyProfit: Long,
    monthlyProfitRate: Double,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = "총 평가금액",
            style = LocalAppTypography.current.bodyLarge.medium,
            color = Natural10,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${formatNumber(totalEvaluationAmount)}P",
            style = LocalAppTypography.current.headlineLarge.bold,
            color = Natural10,
        )

        Spacer(modifier = Modifier.height(34.dp))

        SummaryRow(
            label = "원금",
            value = "${formatNumber(principal)}P",
        )

        Spacer(modifier = Modifier.height(18.dp))

        SummaryProfitRow(
            label = "총 수익",
            amount = totalProfit,
            rate = totalProfitRate,
        )

        Spacer(modifier = Modifier.height(18.dp))

        SummaryProfitRow(
            label = "월간 수익",
            amount = monthlyProfit,
            rate = monthlyProfitRate,
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = Natural10,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.bodySmall.bold,
            color = Natural10,
        )

        Text(
            text = value,
            style = LocalAppTypography.current.bodyLarge.heavy,
            color = valueColor,
        )
    }
}

@Composable
private fun SummaryProfitRow(
    label: String,
    amount: Long,
    rate: Double,
    modifier: Modifier = Modifier,
) {
    val rateColor =
        when {
            rate > 0 -> Red
            rate < 0 -> com.hdb.tourfolio.ui.theme.Blue
            else -> Natural50
        }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.bodySmall.bold,
            color = Natural10,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "${formatNumber(kotlin.math.abs(amount))}P",
                style = LocalAppTypography.current.bodyLarge.bold,
                color = Natural10,
            )

            Text(
                text = buildRateText(rate),
                style = LocalAppTypography.current.bodyLarge.bold,
                color = rateColor,
            )
        }
    }
}

@Composable
private fun HoldingPointCard(
    point: Long,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Primary99)
                .padding(
                    horizontal = 22.dp,
                    vertical = 20.dp,
                ),
    ) {
        Text(
            text = "보유 포인트",
            style = LocalAppTypography.current.bodyLarge.medium,
            color = Natural60,
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = "${formatNumber(point)}P",
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural10,
        )
    }
}

@Composable
private fun HoldingsListHeader(
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
                .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "보유 종목($totalCount)",
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

private fun formatNumber(value: Long): String =
    NumberFormat
        .getNumberInstance(Locale.KOREA)
        .format(value)

private fun buildChangeText(
    amount: Long,
    rate: Double,
): String {
    val amountSign =
        when {
            amount > 0 -> "+"
            amount < 0 -> "-"
            else -> ""
        }

    val rateSign =
        when {
            rate > 0 -> "+"
            rate < 0 -> "-"
            else -> ""
        }

    return "$amountSign${formatNumber(kotlin.math.abs(amount))}P " +
        "($rateSign${String.format(Locale.KOREA, "%.2f", kotlin.math.abs(rate))}%)"
}

private fun buildRateText(rate: Double): String {
    val sign =
        when {
            rate > 0 -> "+"
            rate < 0 -> "-"
            else -> ""
        }

    return "($sign${String.format(Locale.KOREA, "%.2f", kotlin.math.abs(rate))}%)"
}

private fun Long.toPriceChangeType(): PriceChangeType =
    when {
        this > 0 -> PriceChangeType.RISE
        this < 0 -> PriceChangeType.FALL
        else -> PriceChangeType.UNCHANGED
    }

private fun mockPortfolioDto(): PortfolioDto =
    PortfolioDto(
        memberId = 4L,
        username = "투어폴리오유저",
        cashBalance = 20_000L,
        totalStockValue = 12_560_000L,
        totalAssetValue = 12_580_000L,
        totalProfitLossRate = 8.72,
        items =
            listOf(
                PortfolioItemDto(
                    spotId = 1L,
                    spotName = "안압지",
                    quantity = 42,
                    averagePurchasePrice = 8_000L,
                    currentPrice = 9_200L,
                    evaluationAmount = 4_560_000L,
                    profitLossRate = 14.00,
                ),
                PortfolioItemDto(
                    spotId = 2L,
                    spotName = "광안리",
                    quantity = 35,
                    averagePurchasePrice = 10_240L,
                    currentPrice = 11_280L,
                    evaluationAmount = 3_800_000L,
                    profitLossRate = 9.20,
                ),
                PortfolioItemDto(
                    spotId = 3L,
                    spotName = "첨성대",
                    quantity = 28,
                    averagePurchasePrice = 15_430L,
                    currentPrice = 15_000L,
                    evaluationAmount = 4_200_000L,
                    profitLossRate = -2.78,
                ),
            ),
    )

@Preview(
    name = "Holdings Tab Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 915,
)
@Composable
private fun HoldingsTabPreview() {
    TourfolioTheme(
        dynamicColor = false,
    ) {
        HoldingsTabContent(
            uiState = PortfolioUiState.Success(mockPortfolioDto()),
            onStockClick = { _, _, _, _ -> },
            onRetryClick = {},
        )
    }
}
