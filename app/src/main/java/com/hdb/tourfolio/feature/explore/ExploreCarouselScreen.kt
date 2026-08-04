@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore

import android.R.attr.onClick
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.components.CarouselContent
import com.hdb.tourfolio.feature.explore.mock.TourSpotDetailUiModel
import com.hdb.tourfolio.feature.explore.mock.TourSpotListItemUiModel
import com.hdb.tourfolio.feature.explore.mock.TourSpotMockData
import com.hdb.tourfolio.feature.explore.model.ThemeType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlin.math.absoluteValue

private val CAROUSEL_TOUR_SPOT_IDS =
    listOf(
        1L,
        3L,
        6L,
    )

private const val SLIDE_DURATION_MS = 3000L

@Composable
fun ExploreCarouselScreen(
    onFinished: () -> Unit,
    onTourSpotClick: (Long) -> Unit,
) {
    val carouselItems =
        remember {
            CAROUSEL_TOUR_SPOT_IDS.mapNotNull { tourSpotId ->
                TourSpotMockData.findDetailById(tourSpotId)
            }
        }

    val explorePageIndex = carouselItems.size

    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = {
                carouselItems.size + 1
            },
        )

    /*
     * 마지막 Pager 페이지에 완전히 도착하면
     * Navigation이 아니라 부모 상태만 변경합니다.
     */
    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.settledPage == explorePageIndex) {
            onFinished()
        }
    }

    /*
     * 자동 전환
     */
    LaunchedEffect(Unit) {
        while (pagerState.settledPage < explorePageIndex) {
            snapshotFlow {
                pagerState.isScrollInProgress
            }.filter { isScrolling ->
                !isScrolling
            }.first()

            val pageBeforeDelay = pagerState.settledPage

            if (pageBeforeDelay >= explorePageIndex) {
                break
            }

            delay(SLIDE_DURATION_MS)

            val canAutoMove =
                !pagerState.isScrollInProgress &&
                    pagerState.settledPage == pageBeforeDelay

            if (!canAutoMove) {
                continue
            }

            pagerState.animateScrollToPage(
                page = pageBeforeDelay + 1,
            )
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
    ) { page ->
        val pageOffset =
            (
                (pagerState.currentPage - page) +
                    pagerState.currentPageOffsetFraction
            ).absoluteValue

        val pageAlpha =
            1f -
                pageOffset
                    .coerceIn(0f, 1f)
                    .times(0.55f)

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = pageAlpha
                    },
        ) {
            if (page < carouselItems.size) {
                val item = carouselItems[page]

                ExploreCarouselPage(
                    item = item,
                    currentIndex = page,
                    totalCount = carouselItems.size,
                    onClick = {
                        onTourSpotClick(item.id)
                    }
                )

                ExploreCarouselHeader(
                    onSearchClick = onFinished,
                    modifier =
                        Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 16.dp,
                            ),
                )
            } else {
                /*
                 * 스와이프하는 동안 실제 ExploreScreen이 보입니다.
                 */
                ExploreScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun ExploreCarouselPage(
    item: TourSpotDetailUiModel,
    currentIndex: Int,
    totalCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
    ) {
        Image(
            painter =
                painterResource(
                    id = item.imageRes
                ),
            contentDescription = item.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        CarouselContent(
            title = item.title,
            content = item.description,
            place = item.address,
            tags =
                item.tags.map { tag ->
                    tag.displayName
                },
            themeType = item.themeType,
            currentIndex = currentIndex,
            totalCount = totalCount,
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
        )
    }
}

@Composable
private fun ExploreCarouselHeader(
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Tourfolio",
            style =
                LocalAppTypography.current.headlineLarge.bold.copy(
                    color = Natural100,
                ),
        )

        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .clickable(onClick = onSearchClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter =
                    painterResource(
                        id = R.drawable.ic_search,
                    ),
                contentDescription = "탐색 화면으로 이동",
                modifier = Modifier.size(28.dp),
            )
        }
    }
}
