@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.mission.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.feature.mission.presentation.MissionTab
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary10

@Composable
fun MissionTabBar(
    selectedTab: MissionTab,
    onTabSelected: (MissionTab) -> Unit,
    inProgressCount: Int,
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
                    badgeCount = if (tab == MissionTab.IN_PROGRESS) inProgressCount else 0,
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
    badgeCount: Int,
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = tab.label,
                style = LocalAppTypography.current.bodySmall.bold,
                color = if (selected) Primary10 else Natural60,
            )

            if (badgeCount > 0) {
                Spacer(modifier = Modifier.width(6.dp))

                CountBadge(count = badgeCount)
            }
        }

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

@Composable
private fun CountBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(Primary)
                .padding(horizontal = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            style = LocalAppTypography.current.labelSmall.bold,
            color = Natural100,
            textAlign = TextAlign.Center,
        )
    }
}
