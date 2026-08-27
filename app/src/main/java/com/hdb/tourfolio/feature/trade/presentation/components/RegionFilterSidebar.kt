@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural95
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun RegionFilterSidebar(
    regions: List<String>,
    selectedRegion: String,
    onRegionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .width(77.dp)
                .fillMaxHeight()
                .background(Natural95)
                .verticalScroll(rememberScrollState()),
    ) {
        regions.forEach { region ->
            RegionFilterItem(
                region = region,
                selected = region == selectedRegion,
                onClick = {
                    onRegionSelected(region)
                },
            )
        }
    }
}

@Composable
private fun RegionFilterItem(
    region: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .width(77.dp)
                .height(44.dp)
                .background(
                    if (selected) {
                        Natural100
                    } else {
                        Natural95
                    },
                )
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .width(5.dp)
                    .height(44.dp)
                    .background(
                        if (selected) {
                            Primary
                        } else {
                            Natural95
                        },
                    ),
        )

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(68.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = region,
                style =
                    if (selected) {
                        LocalAppTypography.current.bodyLarge.bold
                    } else {
                        LocalAppTypography.current.bodyLarge.medium
                    },
                color =
                    if (selected) {
                        Natural10
                    } else {
                        Natural60
                    },
            )
        }
    }
}

@Preview(
    name = "Region Filter Sidebar",
    showBackground = true,
    widthDp = 120,
    heightDp = 700,
)
@Composable
private fun RegionFilterSidebarPreview() {
    TourfolioTheme(dynamicColor = false) {
        RegionFilterSidebar(
            regions =
                listOf(
                    "전체",
                    "서울",
                    "부산",
                    "대구",
                    "인천",
                    "광주",
                    "대전",
                    "울산",
                    "경기",
                    "강원",
                    "충북",
                    "충남",
                    "전북",
                    "전남",
                    "경북",
                    "경남",
                    "제주",
                ),
            selectedRegion = "전체",
            onRegionSelected = {},
        )
    }
}
