@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreCardUiModel
import com.hdb.tourfolio.ui.components.SpotImageOverlay
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Primary

@Composable
fun TourSpotCarousel(
    items: List<ExploreCardUiModel>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { items.size })
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 22.dp),
            pageSpacing = 12.dp,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val item = items[page]
            SpotImageOverlay(
                hasImage = item.hasImage,
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier.fillMaxWidth().clickable { onItemClick(item.id) },
            ) {
                Box(
                    modifier =
                        Modifier
                            .padding(horizontal = 20.dp)
                            .clip(
                                RoundedCornerShape(
                                    50,
                                ),
                            )
                            .background(
                                Primary,
                            )
                            .padding(
                                horizontal =
                                    8.dp,
                                vertical =
                                    4.dp,
                            ),
                ) {
                    Text(
                        text = item.areaName,
                        style = LocalAppTypography.current.labelSmall.bold,
                        color = Natural100,
                    )
                }

                Text(
                    text = item.title,
                    style = LocalAppTypography.current.titleMedium.bold,
                    color = Natural100,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 3.dp, bottom = 20.dp),
                )
                CarouselPageIndicator(
                    currentPage = pagerState.settledPage,
                    pageCount = items.size,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 22.dp),
                )
            }
        }
    }
}

@Composable
private fun CarouselPageIndicator(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
        modifier,
        horizontalArrangement =
            Arrangement.spacedBy(
                6.dp,
            ),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        repeat(
            pageCount,
        ) { index ->
            val isSelected =
                index == currentPage

            Box(
                modifier =
                    Modifier
                        .height(
                            10.dp,
                        )
                        .then(
                            if (isSelected) {
                                Modifier.width(
                                    30.dp,
                                )
                            } else {
                                Modifier.width(
                                    10.dp,
                                )
                            },
                        )
                        .clip(
                            CircleShape,
                        )
                        .then(
                            if (isSelected) {
                                Modifier.background(
                                    Natural100,
                                )
                            } else {
                                Modifier.border(
                                    width =
                                        1.5.dp,
                                    color =
                                    Natural100,
                                    shape =
                                    CircleShape,
                                )
                            },
                        ),
            )
        }
    }
}
