@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.mission.presentation.components

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
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.feature.mission.presentation.MissionTab
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary10

@Composable
fun MissionTabBar(
    selectedTab: MissionTab,
    onTabSelected: (MissionTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            MissionTab.entries.forEach { tab ->
                MissionTabItem(
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
private fun MissionTabItem(
    tab: MissionTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .height(48.dp)
                .clickable {
                    onClick()
                },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = tab.label,
            style = LocalAppTypography.current.bodySmall.bold,
            color = if (selected) Primary10 else Natural60,
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
