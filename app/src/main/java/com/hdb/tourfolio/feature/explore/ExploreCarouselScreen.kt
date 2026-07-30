@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.components.CarouselContent
import com.hdb.tourfolio.feature.explore.model.ThemeType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlin.math.absoluteValue

private data class CarouselItem(
    val imageRes: Int,
    val title: String,
    val content: String,
    val place: String,
    val tags: List<String>,
    val themeType: ThemeType,
)

private val carouselItems =
    listOf(
        CarouselItem(
            imageRes = R.drawable.bg_explore_gyeongbokgung_demo,
            title = "경복궁",
            content = "조선의 시간을 품은 궁궐\n500년의 역사가 살아 숨 쉬는 곳",
            place = "서울특별시 종로구",
            tags = listOf("역사", "궁궐", "공원", "산책"),
            themeType = ThemeType.HISTORY,
        ),
        CarouselItem(
            imageRes = R.drawable.bg_explore_gyeongbokgung_demo,
            title = "성산일출봉",
            content = "유네스코 세계자연유산\n제주의 상징적인 화산 분화구",
            place = "제주특별자치도 서귀포시",
            tags = listOf("자연", "세계유산", "트레킹"),
            themeType = ThemeType.NATURE,
        ),
        CarouselItem(
            imageRes = R.drawable.bg_explore_gyeongbokgung_demo,
            title = "흰여울길",
            content = "영화 같은 골목길\n부산 영도의 숨겨진 보석",
            place = "부산광역시 영도구",
            tags = listOf("골목", "바다", "사진"),
            themeType = ThemeType.CULTURE,
        ),
    )

private const val SLIDE_DURATION_MS = 3000L

@Composable
fun ExploreCarouselScreen(
    onFinished: () -> Unit,
) {
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
                )

                ExploreCarouselHeader(
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
    item: CarouselItem,
    currentIndex: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        CarouselContent(
            title = item.title,
            content = item.content,
            place = item.place,
            tags = item.tags,
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

        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "search",
            modifier = Modifier.size(28.dp),
        )
    }
}