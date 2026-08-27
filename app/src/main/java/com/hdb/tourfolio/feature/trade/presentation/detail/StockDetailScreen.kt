@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.presentation.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.R
import com.hdb.tourfolio.domain.portfolio.model.PortfolioItem
import com.hdb.tourfolio.domain.stock.model.Stock
import com.hdb.tourfolio.domain.trade.model.TradeResult
import com.hdb.tourfolio.domain.trade.model.TradeType
import com.hdb.tourfolio.feature.trade.presentation.components.AssetChartCard
import com.hdb.tourfolio.feature.trade.presentation.components.AssetPeriod
import com.hdb.tourfolio.feature.trade.presentation.components.AssetPoint
import com.hdb.tourfolio.feature.trade.presentation.components.mockAssetHistory
import com.hdb.tourfolio.ui.theme.Blue
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural20
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Natural99
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary95
import com.hdb.tourfolio.ui.theme.Red
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun StockDetailScreen(
    stockId: Long,
    stockName: String,
    onBackClick: () -> Unit,
    initialCurrentPrice: Long? = null,
    initialPrevPrice: Long? = null,
    modifier: Modifier = Modifier,
    viewModel: StockDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(stockId, stockName) {
        viewModel.processIntent(StockDetailIntent.Enter(spotId = stockId, keyword = stockName))
    }

    StockDetailContent(
        stockId = stockId,
        stockName = stockName,
        onBackClick = onBackClick,
        stock = state.stock,
        initialCurrentPrice = initialCurrentPrice,
        initialPrevPrice = initialPrevPrice,
        selectedPeriod = state.selectedPeriod,
        chartHistory = state.chartHistory,
        onPeriodSelected = { period ->
            viewModel.processIntent(StockDetailIntent.SelectPeriod(spotId = stockId, period = period))
        },
        tradeUiState = state.tradeState,
        onTrade = { type, quantity ->
            viewModel.processIntent(StockDetailIntent.Trade(spotId = stockId, type = type, quantity = quantity))
        },
        onTradeSheetDismiss = { viewModel.processIntent(StockDetailIntent.ResetTradeState) },
        isLiked = state.isLiked,
        onLikeClick = { viewModel.processIntent(StockDetailIntent.ToggleLike(spotId = stockId)) },
        holding = state.holding,
        modifier = modifier,
    )
}

@Composable
private fun StockDetailContent(
    stockId: Long,
    stockName: String,
    onBackClick: () -> Unit,
    stock: Stock?,
    initialCurrentPrice: Long?,
    initialPrevPrice: Long?,
    selectedPeriod: AssetPeriod,
    chartHistory: List<AssetPoint>,
    onPeriodSelected: (AssetPeriod) -> Unit,
    tradeUiState: TradeUiState,
    onTrade: (TradeType, Int) -> Unit,
    onTradeSheetDismiss: () -> Unit,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    holding: PortfolioItem?,
    modifier: Modifier = Modifier,
) {
    var showSellSheet by remember {
        mutableStateOf(false)
    }

    var showBuySheet by remember {
        mutableStateOf(false)
    }

    /*
     * api/stocks(keyword 검색)로 조회한 실제 현재가/전일가가 있으면 그 값을 쓰고,
     * 아직 응답이 도착하지 않은 첫 프레임에는 목록 화면에서 넘겨받은 값으로 대체한다.
     */
    val resolvedCurrentPrice = stock?.currentPrice ?: initialCurrentPrice
    val resolvedPrevPrice = stock?.prevPrice ?: initialPrevPrice

    val currentPrice = resolvedCurrentPrice ?: chartHistory.lastOrNull()?.value ?: 0L
    val prevDayPrice = resolvedPrevPrice ?: currentPrice
    val changeAmount = currentPrice - prevDayPrice
    val changeRate =
        stock?.changeRate ?: if (prevDayPrice != 0L) changeAmount * 100.0 / prevDayPrice else 0.0

    val offeringChangeRate = remember(currentPrice) { mockOfferingChangeRate(currentPrice) }
    val todayVolume = remember(stockId) { mockTodayVolume(stockId) }
    val tourDataIndicators = remember(stockId) { mockTourDataIndicators(stockId) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
    ) {
        StockDetailHeader(
            title = stockName,
            isLiked = isLiked,
            onLikeClick = onLikeClick,
            onBackClick = onBackClick,
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
        ) {
            AssetChartCard(
                label = stockName,
                totalAmount = currentPrice,
                changeAmount = changeAmount,
                changeRate = changeRate,
                assetHistory = chartHistory,
                periods = AssetPeriod.entries,
                selectedPeriod = selectedPeriod,
                onPeriodSelected = onPeriodSelected,
            )

            Spacer(modifier = Modifier.height(24.dp))

            DetailInfoGrid(
                offeringPrice = MOCK_OFFERING_PRICE,
                offeringChangeRate = offeringChangeRate,
                prevDayPrice = prevDayPrice,
                todayVolume = todayVolume,
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "내 보유현황",
                style = LocalAppTypography.current.titleSmall.bold,
                color = Natural10,
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (holding != null) {
                MyHoldingSummary(holding = holding)
            } else {
                NoHoldingNotice()
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "관광 데이터 지표",
                style = LocalAppTypography.current.titleSmall.bold,
                color = Natural10,
            )

            Spacer(modifier = Modifier.height(12.dp))

            TourDataSection(
                demandIntensity = tourDataIndicators.demandIntensity,
                visitorForecast = tourDataIndicators.visitorForecast,
                resourceDemand = tourDataIndicators.resourceDemand,
            )
        }

        StockActionBar(
            onSellClick = {
                showSellSheet = true
            },
            onBuyClick = {
                showBuySheet = true
            },
        )
    }

    if (showSellSheet) {
        TradeBottomSheet(
            title = "관광지 주식 판매",
            priceLabel = "판매할 가격",
            quantityHintPrefix = "판매가능 최대",
            totalLabel = "총 판매금액",
            buttonLabel = "판매하기",
            buttonColor = Natural20,
            currentPrice = currentPrice,
            avgPrice = holding?.averagePurchasePrice ?: 0L,
            maxQuantity = holding?.quantity ?: 0,
            tradeUiState = tradeUiState,
            onDismiss = {
                showSellSheet = false
                onTradeSheetDismiss()
            },
            onConfirmClick = { quantity ->
                onTrade(TradeType.SELL, quantity)
            },
        )
    }

    if (showBuySheet) {
        TradeBottomSheet(
            title = "관광지 주식 구매",
            priceLabel = "구매할 가격",
            quantityHintPrefix = "구매가능 최대",
            totalLabel = "총 구매금액",
            buttonLabel = "구매하기",
            buttonColor = Primary,
            currentPrice = currentPrice,
            avgPrice = holding?.averagePurchasePrice ?: 0L,
            maxQuantity = holding?.quantity ?: 0,
            tradeUiState = tradeUiState,
            onDismiss = {
                showBuySheet = false
                onTradeSheetDismiss()
            },
            onConfirmClick = { quantity ->
                onTrade(TradeType.BUY, quantity)
            },
        )
    }
}

@Composable
private fun DetailInfoGrid(
    offeringPrice: Long,
    offeringChangeRate: Double,
    prevDayPrice: Long,
    todayVolume: Int,
    modifier: Modifier = Modifier,
) {
    val offeringChangeColor =
        when {
            offeringChangeRate > 0 -> Red
            offeringChangeRate < 0 -> Blue
            else -> Natural50
        }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DetailInfoTile(
                label = "공모가",
                value = "%,dP".format(offeringPrice),
                modifier = Modifier.weight(1f),
            )

            DetailInfoTile(
                label = "공모가 대비",
                value = "%+.2f%%".format(offeringChangeRate),
                valueColor = offeringChangeColor,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DetailInfoTile(
                label = "어제 가격",
                value = "%,dP".format(prevDayPrice),
                modifier = Modifier.weight(1f),
            )

            DetailInfoTile(
                label = "오늘 거래량",
                value = "%,d주".format(todayVolume),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DetailInfoTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Natural10,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Natural99)
                .padding(horizontal = 18.dp, vertical = 18.dp),
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.bodyLarge.medium,
            color = Natural50,
            maxLines = 1,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = value,
            style = LocalAppTypography.current.titleMedium.bold,
            color = valueColor,
            maxLines = 1,
        )
    }
}

@Composable
private fun NoHoldingNotice(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Natural99)
                .padding(horizontal = 18.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "보유 중인 수량이 없습니다.",
            style = LocalAppTypography.current.bodyLarge.medium,
            color = Natural50,
        )
    }
}

@Composable
private fun MyHoldingSummary(
    holding: PortfolioItem,
    modifier: Modifier = Modifier,
) {
    val profitAmount = holding.evaluationAmount - holding.averagePurchasePrice * holding.quantity

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DetailInfoTile(
            label = "1주 평균",
            value = "%,dP".format(holding.averagePurchasePrice),
        )

        DetailInfoTile(
            label = "보유 수량",
            value = "%,d주".format(holding.quantity),
        )

        TotalAmountCard(
            totalAmount = holding.evaluationAmount,
            profitAmount = profitAmount,
            profitRate = holding.profitLossRate,
        )
    }
}

@Composable
private fun TotalAmountCard(
    totalAmount: Long,
    profitAmount: Long,
    profitRate: Double,
    modifier: Modifier = Modifier,
) {
    val profitColor =
        when {
            profitAmount > 0 -> Red
            profitAmount < 0 -> Blue
            else -> Natural50
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Primary95)
                .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "총 금액",
                style = LocalAppTypography.current.bodyLarge.medium,
                color = Natural50,
            )

            Text(
                text = "%,dP".format(totalAmount),
                style = LocalAppTypography.current.titleSmall.bold,
                color = Natural10,
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "%+,dP (%+.2f%%)".format(profitAmount, profitRate),
            style = LocalAppTypography.current.bodyLarge.bold,
            color = profitColor,
        )
    }
}

@Composable
private fun TourDataSection(
    demandIntensity: Int,
    visitorForecast: Int,
    resourceDemand: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TourDataCard(
            label = "수요 강도",
            filledCount = demandIntensity,
        )

        TourDataCard(
            label = "방문자 예측",
            filledCount = visitorForecast,
        )

        TourDataCard(
            label = "자원 수요",
            filledCount = resourceDemand,
        )
    }
}

@Composable
private fun TourDataCard(
    label: String,
    filledCount: Int,
    modifier: Modifier = Modifier,
    totalCount: Int = 10,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Natural99)
                .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.bodyLarge.bold,
            color = Natural10,
        )

        DotMeter(
            filledCount = filledCount,
            totalCount = totalCount,
        )
    }
}

@Composable
private fun DotMeter(
    filledCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalCount) { index ->
            Box(
                modifier =
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .then(
                            if (index < filledCount) {
                                Modifier.background(Primary)
                            } else {
                                Modifier.border(1.dp, Natural90, CircleShape)
                            },
                        ),
            )
        }
    }
}

@Composable
private fun StockActionBar(
    onSellClick: () -> Unit,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().background(Natural100),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Natural90),
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StockActionButton(
                label = "판매하기",
                containerColor = Natural20,
                onClick = onSellClick,
                modifier = Modifier.weight(1f),
            )

            StockActionButton(
                label = "구매하기",
                containerColor = Primary,
                onClick = onBuyClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StockActionButton(
    label: String,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(containerColor)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.bodyLarge.bold,
            color = Natural100,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TradeBottomSheet(
    title: String,
    priceLabel: String,
    quantityHintPrefix: String,
    totalLabel: String,
    buttonLabel: String,
    buttonColor: Color,
    currentPrice: Long,
    avgPrice: Long,
    maxQuantity: Int,
    tradeUiState: TradeUiState,
    onDismiss: () -> Unit,
    onConfirmClick: (quantity: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var quantity by remember {
        mutableIntStateOf(minOf(1, maxQuantity))
    }

    val totalAmount = currentPrice * quantity
    val profitAmount = (currentPrice - avgPrice) * quantity
    val profitColor =
        when {
            profitAmount > 0 -> Red
            profitAmount < 0 -> Blue
            else -> Natural50
        }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Natural100,
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
        ) {
            Text(
                text = title,
                style = LocalAppTypography.current.titleSmall.bold,
                color = Natural10,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Natural90),
            )

            Spacer(modifier = Modifier.height(20.dp))

            TradeSectionLabel(text = priceLabel)

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Natural99)
                        .padding(horizontal = 18.dp, vertical = 18.dp),
            ) {
                Text(
                    text = "%,dP".format(currentPrice),
                    style = LocalAppTypography.current.titleSmall.bold,
                    color = Natural10,
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            TradeSectionLabel(text = "수량")

            Spacer(modifier = Modifier.height(10.dp))

            QuantityStepper(
                quantity = quantity,
                onIncrement = {
                    if (quantity < maxQuantity) quantity++
                },
                onDecrement = {
                    if (quantity > 1) quantity--
                },
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$quantityHintPrefix ${maxQuantity}주",
                style = LocalAppTypography.current.labelLarge.medium,
                color = Natural50,
            )

            Spacer(modifier = Modifier.height(28.dp))

            TradeSectionLabel(text = totalLabel)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Primary95)
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "%,dP".format(totalAmount),
                    style = LocalAppTypography.current.titleSmall.bold,
                    color = Natural10,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "%+,dP".format(profitAmount),
                    style = LocalAppTypography.current.bodyLarge.bold,
                    color = profitColor,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Natural90),
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (tradeUiState) {
                is TradeUiState.Success -> {
                    TradeResultSummary(result = tradeUiState.result)

                    Spacer(modifier = Modifier.height(16.dp))

                    StockActionButton(
                        label = "확인",
                        containerColor = buttonColor,
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                else -> {
                    val isLoading = tradeUiState is TradeUiState.Loading

                    StockActionButton(
                        label = if (isLoading) "처리 중..." else buttonLabel,
                        containerColor = buttonColor,
                        onClick = {
                            if (!isLoading) {
                                onConfirmClick(quantity)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (tradeUiState is TradeUiState.Error) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = tradeUiState.message,
                            style = LocalAppTypography.current.bodySmall.bold,
                            color = Red,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TradeResultSummary(
    result: TradeResult,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Primary95)
                .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "체결이 완료되었습니다",
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural10,
        )

        Text(
            text = "체결가 %,dP · %,d주".format(result.price, result.quantity),
            style = LocalAppTypography.current.bodySmall.medium,
            color = Natural50,
        )

        Text(
            text = "총 %,dP".format(result.totalAmount),
            style = LocalAppTypography.current.bodyLarge.bold,
            color = Natural10,
        )
    }
}

@Composable
private fun TradeSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = LocalAppTypography.current.bodyLarge.bold,
        color = Natural10,
        modifier = modifier,
    )
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Natural99)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperButton(
            symbol = "+",
            onClick = onIncrement,
        )

        Text(
            text = "$quantity 주",
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural10,
        )

        StepperButton(
            symbol = "−",
            onClick = onDecrement,
        )
    }
}

@Composable
private fun StepperButton(
    symbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Natural90)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural10,
        )
    }
}

@Composable
private fun StockDetailHeader(
    title: String,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "←",
            style = LocalAppTypography.current.titleMedium.bold,
            color = Natural10,
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .clickable(onClick = onBackClick),
        )

        Text(
            text = title,
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural10,
            textAlign = TextAlign.Center,
        )

        Image(
            painter =
                painterResource(
                    id = if (isLiked) R.drawable.ic_like_full else R.drawable.ic_like_empty,
                ),
            contentDescription = "찜하기",
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .size(24.dp)
                    .clickable(onClick = onLikeClick),
        )
    }
}

@Preview(
    name = "Stock Detail Screen Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 700,
)
@Composable
private fun StockDetailScreenPreview() {
    TourfolioTheme(dynamicColor = false) {
        StockDetailContent(
            stockId = 1L,
            stockName = "경복궁",
            stock = null,
            initialCurrentPrice = 18_900L,
            initialPrevPrice = 17_780L,
            selectedPeriod = AssetPeriod.WEEK,
            chartHistory = mockAssetHistory(period = AssetPeriod.WEEK, seed = 1L),
            onPeriodSelected = {},
            tradeUiState = TradeUiState.Idle,
            onTrade = { _, _ -> },
            onTradeSheetDismiss = {},
            isLiked = false,
            onLikeClick = {},
            onBackClick = {},
            holding = null,
        )
    }
}
