@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.presentation.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.domain.watchlist.model.WatchlistItem
import com.hdb.tourfolio.feature.trade.presentation.components.PriceChangeType
import com.hdb.tourfolio.feature.trade.presentation.components.TourStockCard
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

private data class WatchlistCardItem(
    val entryId: Long,
    val spotId: Long,
    val title: String,
    val region: String,
    val currentPrice: Long,
    val prevPrice: Long,
    val priceText: String,
    val changeText: String,
    val changeType: PriceChangeType,
)

@Composable
fun WatchlistTab(
    modifier: Modifier = Modifier,
    onStockClick: (Long, String, Long?, Long?) -> Unit = { _, _, _, _ -> },
    viewModel: TradeWatchlistViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.processIntent(TradeWatchlistIntent.FetchWatchlist)
    }

    WatchlistTabContent(
        modifier = modifier,
        uiState = state.watchlistResult,
        onStockClick = onStockClick,
        onRetryClick = { viewModel.processIntent(TradeWatchlistIntent.FetchWatchlist) },
        onLikeClick = { spotId -> viewModel.processIntent(TradeWatchlistIntent.ToggleLike(spotId)) },
    )
}

@Composable
private fun WatchlistTabContent(
    uiState: WatchlistUiState,
    onStockClick: (Long, String, Long?, Long?) -> Unit,
    onRetryClick: () -> Unit,
    onLikeClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is WatchlistUiState.Loading -> {
            LoadingWatchlist(modifier = modifier.fillMaxSize().background(Natural100))
        }

        is WatchlistUiState.Error -> {
            ErrorWatchlist(
                message = uiState.message,
                onRetryClick = onRetryClick,
                modifier = modifier.fillMaxSize().background(Natural100),
            )
        }

        is WatchlistUiState.Success -> {
            val watchlistItems = uiState.items.map { it.toWatchlistCardItem() }

            if (watchlistItems.isEmpty()) {
                EmptyWatchlist(modifier = modifier.fillMaxSize().background(Natural100))
            } else {
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
                        Text(
                            text = "관심 종목 ${watchlistItems.size}개",
                            style = LocalAppTypography.current.bodyLarge.bold,
                            color = Natural10,
                        )
                    }

                    items(
                        items = watchlistItems,
                        key = { item -> item.entryId },
                    ) { item ->
                        TourStockCard(
                            title = item.title,
                            subtitle = item.region,
                            priceText = item.priceText,
                            changeText = item.changeText,
                            changeType = item.changeType,
                            isLiked = true,
                            onLikeClick = { onLikeClick(item.spotId) },
                            onClick = {
                                onStockClick(item.spotId, item.title, item.currentPrice, item.prevPrice)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingWatchlist(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun ErrorWatchlist(
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
                text = "관심 종목을 불러오지 못했습니다.",
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
private fun EmptyWatchlist(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "관심 등록한 관광지가 없습니다.",
            style = LocalAppTypography.current.bodyLarge.medium,
            color = Natural50,
        )
    }
}

private fun WatchlistItem.toWatchlistCardItem(): WatchlistCardItem {
    val changeType =
        when {
            changeRate > 0 -> PriceChangeType.RISE
            changeRate < 0 -> PriceChangeType.FALL
            else -> PriceChangeType.UNCHANGED
        }

    return WatchlistCardItem(
        entryId = id,
        spotId = spotId,
        title = spotName,
        region = region,
        currentPrice = currentPrice,
        prevPrice = prevPrice,
        priceText = "%,dP".format(currentPrice),
        changeText = "%+.2f%%".format(changeRate),
        changeType = changeType,
    )
}

private fun mockWatchlistItems(): List<WatchlistItem> =
    listOf(
        WatchlistItem(1L, 101L, "경복궁", "서울", "역사", 18_900L, 5.59, 17_900L),
        WatchlistItem(2L, 102L, "해운대", "부산", "자연", 9_800L, 10.36, 8_900L),
        WatchlistItem(3L, 103L, "첨성대", "경주", "역사", 12_050L, -4.59, 12_650L),
    )

@Preview(
    name = "Watchlist Tab Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 500,
)
@Composable
private fun WatchlistTabPreview() {
    TourfolioTheme(dynamicColor = false) {
        WatchlistTabContent(
            uiState = WatchlistUiState.Success(mockWatchlistItems()),
            onStockClick = { _, _, _, _ -> },
            onRetryClick = {},
            onLikeClick = {},
        )
    }
}
