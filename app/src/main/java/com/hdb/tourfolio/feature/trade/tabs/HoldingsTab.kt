@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.tabs

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
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
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.trade.components.FilterDropdown
import com.hdb.tourfolio.feature.trade.components.PriceChangeType
import com.hdb.tourfolio.feature.trade.components.TourStockCard
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary99
import com.hdb.tourfolio.ui.theme.Red
import com.hdb.tourfolio.ui.theme.TourfolioTheme
import java.text.NumberFormat
import java.util.Locale

private data class HoldingStockItem(
    val id: Long,
    val title: String,
    val region: String,
    val evaluationAmount: Long,
    val profitAmount: Long,
    val profitRate: Double,
    val quantity: Int,
    @DrawableRes val imageRes: Int,
)

@Composable
fun HoldingsTab(modifier: Modifier = Modifier) {
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

   /*
    *  임시데이터
    */
    val holdingStocks =
        remember {
            listOf(
                HoldingStockItem(
                    id = 1L,
                    title = "안압지",
                    region = "경주",
                    evaluationAmount = 4_560_000L,
                    profitAmount = 560_000L,
                    profitRate = 14.00,
                    quantity = 42,
                    imageRes = R.drawable.bg_cheomseongdae_demo,
                ),
                HoldingStockItem(
                    id = 2L,
                    title = "광안리",
                    region = "부산",
                    evaluationAmount = 3_800_000L,
                    profitAmount = 320_000L,
                    profitRate = 9.20,
                    quantity = 35,
                    imageRes = R.drawable.bg_huinnyeoul_demo,
                ),
                HoldingStockItem(
                    id = 3L,
                    title = "첨성대",
                    region = "경주",
                    evaluationAmount = 4_200_000L,
                    profitAmount = -120_000L,
                    profitRate = -2.78,
                    quantity = 28,
                    imageRes = R.drawable.bg_cheomseongdae_demo,
                ),
            )
        }

    /*
     * 현재는 임시 데이터에 대해 로컬 정렬
     */
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

    // 임시 데이터
    val monthlyProfit = 240_000L
    val monthlyProfitRate = 2.15
    val holdingPoint = 20_000L

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

                    /*
                     * 서버 정렬을 적용한다면 다음과 같이 ViewModel에 전달
                     * viewModel.changeSortOption(option)
                     */
                },
            )
        }

        items(
            items = sortedHoldingStocks,
            key = { item -> item.id },
        ) { item ->
            TourStockCard(
                title = item.title,
                subtitle = item.region,
                imageRes = item.imageRes,
                priceText = "${formatNumber(item.evaluationAmount)}P",
                changeText =
                    buildChangeText(
                        amount = item.profitAmount,
                        rate = item.profitRate,
                    ),
                changeType = item.profitAmount.toPriceChangeType(),
                onClick = {
                    // 상세화면이 없다면 제거
                },
            )
        }
    }
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

private fun Long.toChangeColor(): androidx.compose.ui.graphics.Color =
    when {
        this > 0 -> Red
        this < 0 -> com.hdb.tourfolio.ui.theme.Blue
        else -> Natural50
    }

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
        HoldingsTab()
    }
}
