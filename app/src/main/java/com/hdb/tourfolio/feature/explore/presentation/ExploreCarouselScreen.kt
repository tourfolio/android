@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.feature.explore.presentation.components.CarouselContent
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreMainCardUiModel
import com.hdb.tourfolio.ui.components.CommonHeader
import com.hdb.tourfolio.ui.components.CommonHeaderType
import com.hdb.tourfolio.ui.components.SpotImageOverlay
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

private const val SLIDE_DURATION_MS = 3000L

@Composable
fun ExploreCarouselScreen(
    onFinished: () -> Unit,
    onTourSpotClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreCarouselViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val mainCards = state.mainCards) {
        ExploreMainCardsUiState.Loading -> {
            ExploreCarouselLoading(modifier = modifier)
        }

        is ExploreMainCardsUiState.Error -> {
            ExploreCarouselError(
                message = mainCards.message,
                onRetryClick = {
                    viewModel.processIntent(ExploreCarouselIntent.FetchMainCards)
                },
                modifier = modifier,
            )
        }

        is ExploreMainCardsUiState.Success -> {
            ExploreCarouselContent(
                carouselItems = mainCards.cards,
                onFinished = onFinished,
                onTourSpotClick = onTourSpotClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ExploreCarouselContent(
    carouselItems: List<ExploreMainCardUiModel>,
    onFinished: () -> Unit,
    onTourSpotClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (carouselItems.isEmpty()) {
        LaunchedEffect(Unit) {
            onFinished()
        }

        return
    }

    val explorePageIndex = carouselItems.size

    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = { carouselItems.size + 1 },
        )

    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.settledPage == explorePageIndex) {
            onFinished()
        }
    }

    LaunchedEffect(carouselItems) {
        while (pagerState.settledPage < explorePageIndex) {
            snapshotFlow { pagerState.isScrollInProgress }
                .filter { isScrolling -> !isScrolling }
                .first()

            val pageBeforeDelay = pagerState.settledPage

            if (pageBeforeDelay >= explorePageIndex) {
                break
            }

            delay(SLIDE_DURATION_MS)

            val canAutoMove =
                !pagerState.isScrollInProgress && pagerState.settledPage == pageBeforeDelay

            if (!canAutoMove) {
                continue
            }

            pagerState.animateScrollToPage(page = pageBeforeDelay + 1)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
    ) { page ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            if (page < carouselItems.size) {
                val item = carouselItems[page]

                Column(modifier = Modifier.fillMaxSize()) {
                    CommonHeader(
                        type = CommonHeaderType.SEARCH,
                        onSearchClick = onFinished,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    )
                    ExploreCarouselPage(
                        item = item,
                        currentIndex = page,
                        totalCount = carouselItems.size,
                        onClick = { onTourSpotClick(item.id) },
                    )
                }
            } else {
                ExploreScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun ExploreCarouselPage(
    item: ExploreMainCardUiModel,
    currentIndex: Int,
    totalCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val viewportHeight = maxHeight
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min = viewportHeight),
            verticalArrangement = Arrangement.Center,
        ) {
            SpotImageOverlay(
                hasImage = item.hasImage,
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
            ) {
                CarouselContent(
                    title = item.title,
                    content = item.subTitle,
                    place = item.location,
                    tags = item.tags,
                    currentIndex = currentIndex,
                    totalCount = totalCount,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun ExploreCarouselLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(Natural100),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun ExploreCarouselError(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize().background(Natural100),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "탐색 정보를 불러오지 못했습니다.",
                style = LocalAppTypography.current.bodyLarge.bold.copy(color = Natural10),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = LocalAppTypography.current.bodySmall.medium.copy(color = Natural60),
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onRetryClick) {
                Text(
                    text = "다시 시도",
                    style = LocalAppTypography.current.bodySmall.bold.copy(color = Primary),
                )
            }
        }
    }
}
