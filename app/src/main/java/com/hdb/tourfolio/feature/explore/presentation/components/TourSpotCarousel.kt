@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreCardUiModel
import com.hdb.tourfolio.ui.components.SpotImage
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Primary
import kotlin.math.absoluteValue

@Composable
fun TourSpotCarousel(
    items: List<ExploreCardUiModel>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) {
        return
    }

    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = {
                items.size
            },
        )

    BoxWithConstraints(
        modifier =
            modifier.fillMaxWidth(),
    ) {
        val cardWidth =
            367.dp

        val horizontalPadding =
            (
                (maxWidth - cardWidth) /
                    2
            ).coerceAtLeast(
                0.dp,
            )

        HorizontalPager(
            state =
            pagerState,
            pageSize =
                PageSize.Fixed(
                    cardWidth,
                ),
            contentPadding =
                PaddingValues(
                    horizontal =
                    horizontalPadding,
                ),
            pageSpacing =
                0.dp,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        370.dp,
                    ),
        ) { page ->
            val offset =
                (
                    (pagerState.currentPage - page) +
                        pagerState.currentPageOffsetFraction
                ).absoluteValue.coerceIn(
                    0f,
                    1f,
                )

            val scale =
                1f -
                    (offset * 0.08f)

            val alpha =
                1f -
                    (offset * 0.25f)

            TourSpotCarouselCard(
                item =
                    items[page],
                currentPage =
                    pagerState.settledPage,
                pageCount =
                    items.size,
                onClick = {
                    onItemClick(
                        items[page].id,
                    )
                },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX =
                                scale
                            scaleY =
                                scale
                            this.alpha =
                                alpha
                        },
            )
        }
    }
}

@Composable
private fun TourSpotCarouselCard(
    item: ExploreCardUiModel,
    currentPage: Int,
    pageCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(
                    RoundedCornerShape(
                        20.dp,
                    ),
                )
                .clickable(
                    onClick =
                    onClick,
                ),
    ) {
        SpotImage(
            hasImage = item.hasImage,
            model = item.imageUrl,
            contentDescription =
                item.title,
            modifier =
                Modifier.fillMaxSize(),
            contentScale =
                ContentScale.Crop,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Black.copy(alpha = if (item.hasImage) 0.72f else 0f),
                                    ),
                            ),
                    ),
        )

        Column(
            modifier =
                Modifier
                    .align(
                        Alignment.BottomStart,
                    )
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 50.dp,
                    ),
        ) {
            /*
             * 서버 areaName
             */
            Box(
                modifier =
                    Modifier
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
                                10.dp,
                            vertical =
                                6.dp,
                        ),
            ) {
                Text(
                    text =
                        item.areaName,
                    style =
                        LocalAppTypography
                            .current
                            .bodySmall
                            .bold
                            .copy(
                                color =
                                    if (item.hasImage) Natural100 else Color.Gray,
                            ),
                )
            }

            Spacer(
                modifier =
                    Modifier.height(
                        10.dp,
                    ),
            )

            Text(
                text =
                    item.title,
                style =
                    LocalAppTypography
                        .current
                        .titleMedium
                        .bold
                        .copy(
                            color =
                                if (item.hasImage) Natural100 else Color.Gray,
                        ),
                maxLines =
                1,
                overflow =
                    TextOverflow.Ellipsis,
            )
        }

        CarouselPageIndicator(
            currentPage =
            currentPage,
            pageCount =
            pageCount,
            modifier =
                Modifier
                    .align(
                        Alignment.BottomCenter,
                    )
                    .padding(
                        bottom = 22.dp,
                    ),
        )
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
