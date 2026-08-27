@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural70
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Primary

enum class SearchFilterTab(
    val displayName: String,
) {
    TAG("태그별"),
    THEME("테마별"),
    REGION("지역별"),
}

@Composable
fun SearchFilterTabBar(
    selectedTab: SearchFilterTab,
    onTabSelected: (SearchFilterTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            SearchFilterTab.entries.forEach { tab ->
                SearchFilterTabItem(
                    tab = tab,
                    selected = selectedTab == tab,
                    onClick = {
                        onTabSelected(tab)
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Natural90),
        )
    }
}

@Composable
private fun SearchFilterTabItem(
    tab: SearchFilterTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textColor: Color =
        if (selected) {
            Natural10
        } else {
            Natural70
        }

    Box(
        modifier =
            modifier
                .height(48.dp)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = tab.displayName,
            style =
                if (selected) {
                    LocalAppTypography.current.bodySmall.bold
                } else {
                    LocalAppTypography.current.bodySmall.medium
                },
            color = textColor,
        )

        if (selected) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Primary),
            )
        }
    }
}
