@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hdb.tourfolio.feature.trade.components.TradeHeader
import com.hdb.tourfolio.feature.trade.components.TradeTabBar
import com.hdb.tourfolio.feature.trade.tabs.ExploreTab
import com.hdb.tourfolio.feature.trade.tabs.HoldingsTab
import com.hdb.tourfolio.feature.trade.tabs.HomeTab
import com.hdb.tourfolio.feature.trade.tabs.WatchlistTab
import com.hdb.tourfolio.ui.theme.Surface
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun TradeScreen(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
) {
    var selectedTab by rememberSaveable {
        mutableStateOf(TradeTab.HOME)
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
        )

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
                )
            }

            TradeTab.EXPLORE -> {
                ExploreTab(
                    modifier = Modifier.weight(1f),
                )
            }

            TradeTab.HOLDINGS -> {
                HoldingsTab(
                    modifier = Modifier.weight(1f),
                )
            }

            TradeTab.WATCHLIST -> {
                WatchlistTab(
                    modifier = Modifier.weight(1f),
                )
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
