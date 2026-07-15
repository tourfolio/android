@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural50

@Composable
fun WatchlistTab(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "관심 목록 화면",
            style = LocalAppTypography.current.bodyLarge.medium,
            color = Natural50,
        )
    }
}
