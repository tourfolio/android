@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hdb.tourfolio.feature.trade.presentation.auth.AuthOverlay
import com.hdb.tourfolio.feature.trade.presentation.components.TradeHeader
import com.hdb.tourfolio.feature.trade.presentation.components.TradeTabBar
import com.hdb.tourfolio.feature.trade.presentation.tabs.HoldingsTab
import com.hdb.tourfolio.feature.trade.presentation.tabs.HomeTab
import com.hdb.tourfolio.feature.trade.presentation.tabs.TradeExploreTab
import com.hdb.tourfolio.feature.trade.presentation.tabs.WatchlistTab
import com.hdb.tourfolio.ui.theme.Surface
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun TradeScreen(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onStockClick: (Long, String, Long?, Long?) -> Unit = { _, _, _, _ -> },
) {
    var selectedTab by rememberSaveable {
        mutableStateOf(TradeTab.HOME)
    }

    var showAuthScreen by remember {
        mutableStateOf(false)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Surface),
    ) {
        TradeHeader(
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onProfileClick = {
                showAuthScreen = true
            },
        )

        if (showAuthScreen) {
            AuthOverlay(
                onBackClick = {
                    showAuthScreen = false
                },
                modifier = Modifier.weight(1f),
            )
        } else {
            TradeTabBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                },
            )

            when (selectedTab) {
                TradeTab.HOME -> {
                    HomeTab(
                        modifier = Modifier.weight(1f),
                        onStockClick = onStockClick,
                    )
                }

                TradeTab.EXPLORE -> {
                    TradeExploreTab(
                        modifier = Modifier.weight(1f),
                        onStockClick = onStockClick,
                    )
                }

                TradeTab.HOLDINGS -> {
                    HoldingsTab(
                        modifier = Modifier.weight(1f),
                        onStockClick = onStockClick,
                    )
                }

                TradeTab.WATCHLIST -> {
                    WatchlistTab(
                        modifier = Modifier.weight(1f),
                        onStockClick = onStockClick,
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Trade Screen Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 915,
)
@Composable
private fun TradeScreenPreview() {
    TourfolioTheme(
        dynamicColor = false,
    ) {
        TradeScreen()
    }
}
