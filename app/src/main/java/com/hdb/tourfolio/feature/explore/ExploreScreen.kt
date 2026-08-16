@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.components.PlaceCard
import com.hdb.tourfolio.feature.explore.components.RegionCard
import com.hdb.tourfolio.feature.explore.components.SearchBar
import com.hdb.tourfolio.feature.explore.components.TourSpotCard
import com.hdb.tourfolio.feature.explore.components.TourSpotCarousel
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary

data class ThemeTravelItem(
    val id: Long,
    val title: String,
    val places: Int,
    val imageRes: Int,
)

@Composable
fun ExploreScreen(
    onCityTravelClick: (Long) -> Unit = {},
    onTourSpotClick: (Long) -> Unit = {},
    onSearchClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = hiltViewModel(),
) {
    val exploreCardsUiState by
        viewModel.exploreCardsUiState
            .collectAsStateWithLifecycle()

    val trendingUiState by
        viewModel.trendingUiState
            .collectAsStateWithLifecycle()

    val themeTravelItems =
        remember {
            listOf(
                ThemeTravelItem(
                    id = 1L,
                    title = "서울로 떠나는 역사탐방",
                    places = 10,
                    imageRes = R.drawable.bg_seoul_demo,
                ),
                ThemeTravelItem(
                    id = 3L,
                    title = "경주로 떠나는 야경명소",
                    places = 10,
                    imageRes = R.drawable.bg_gyeongju_demo,
                ),
                ThemeTravelItem(
                    id = 2L,
                    title = "여름에 꼭 봐야할 부산 명소",
                    places = 6,
                    imageRes = R.drawable.bg_busan_demo,
                ),
            )
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp),
    ) {
        ExploreHeader(
            modifier =
                Modifier.padding(
                    start = 22.dp,
                    top = 20.dp,
                    end = 22.dp,
                ),
        )

        Spacer(modifier = Modifier.height(28.dp))

        SearchBar(
            value = "",
            onValueChange = {},
            readOnly = true,
            onClick = onSearchClick,
            modifier =
                Modifier.padding(horizontal = 22.dp),
        )

        Spacer(modifier = Modifier.height(18.dp))

        when (
            val state =
                exploreCardsUiState
        ) {
            ExploreCardsUiState.Loading -> {
                ExploreSectionLoading(
                    height = 370.dp,
                )
            }

            is ExploreCardsUiState.Error -> {
                ExploreSectionError(
                    message =
                        state.message,
                    onRetryClick = {
                        viewModel.fetchExploreCards()
                    },
                )
            }

            is ExploreCardsUiState.Success -> {
                TourSpotCarousel(
                    items =
                        state.featuredCards,
                    onItemClick =
                    onTourSpotClick,
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        ExploreSectionTitle(
            title = "지역별 맞춤 여행지",
            modifier = Modifier.padding(horizontal = 22.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier =
                Modifier.padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp),
        ) {
            themeTravelItems.forEach { item ->
                PlaceCard(
                    title = item.title,
                    places = item.places,
                    imageRes = item.imageRes,
                    onClick = {
                        if (item.id == 2L) {
                            onCityTravelClick(item.id)
                        }
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(34.dp))

        ExploreSectionTitle(
            title = "놓치면 아쉬운 추천 명소",
            modifier = Modifier.padding(horizontal = 22.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (
            val state =
                exploreCardsUiState
        ) {
            ExploreCardsUiState.Loading -> {
                ExploreSectionLoading(
                    height = 220.dp,
                )
            }

            is ExploreCardsUiState.Error -> {
                ExploreSectionError(
                    message =
                        state.message,
                    onRetryClick = {
                        viewModel.fetchExploreCards()
                    },
                )
            }

            is ExploreCardsUiState.Success -> {
                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            12.dp,
                        ),
                    contentPadding =
                        PaddingValues(
                            horizontal = 22.dp,
                        ),
                ) {
                    items(
                        items =
                            state.recommendedCards,
                        key = { item ->
                            item.id
                        },
                    ) { item ->
                        TourSpotCard(
                            id =
                                item.id,
                            title =
                                item.title,
                            content =
                                item.description,
                            tags =
                                item.tags,
                            imageUrl =
                                item.imageUrl,
                            onClick =
                            onTourSpotClick,
                            modifier =
                                Modifier.width(
                                    360.dp,
                                ),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(34.dp))

        ExploreSectionTitle(
            title = "지금 뜨는 여행지",
            modifier = Modifier.padding(horizontal = 22.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (
            val state =
                trendingUiState
        ) {
            ExploreTrendingUiState.Loading -> {
                ExploreSectionLoading(
                    height = 245.dp,
                )
            }

            is ExploreTrendingUiState.Error -> {
                ExploreSectionError(
                    message =
                        state.message,
                    onRetryClick = {
                        viewModel.fetchTrendingCards()
                    },
                )
            }

            is ExploreTrendingUiState.Success -> {
                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp,
                        ),
                    contentPadding =
                        PaddingValues(
                            horizontal = 22.dp,
                        ),
                ) {
                    items(
                        items =
                            state.cards,
                        key = { item ->
                            item.id
                        },
                    ) { item ->
                        RegionCard(
                            id =
                                item.id,
                            title =
                                item.title,
                            regionName =
                                item.areaName,
                            imageUrl =
                                item.imageUrl,
                            onClick =
                            onTourSpotClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreHeader(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Tourfolio",
            style =
                LocalAppTypography.current.headlineLarge.heavy.copy(
                    color = Natural10,
                ),
        )

        Image(
            painter = painterResource(id = R.drawable.ic_bell_black),
            contentDescription = "알림",
            modifier = Modifier.size(27.dp),
        )
    }
}

@Composable
private fun ExploreSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    showMoreIcon: Boolean = false,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style =
                LocalAppTypography.current.titleMedium.bold.copy(
                    color = Natural10,
                ),
        )

        if (showMoreIcon) {
            Image(
                painter =
                    painterResource(
                        id = R.drawable.ic_arrow_down_gray,
                    ),
                contentDescription = "더보기",
                modifier =
                    Modifier
                        .size(16.dp)
                        .rotate(-90f),
            )
        }
    }
}

@Composable
private fun ExploreSectionLoading(height: androidx.compose.ui.unit.Dp) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    height,
                ),
        contentAlignment =
            Alignment.Center,
    ) {
        CircularProgressIndicator(
            color =
            Primary,
        )
    }
}

@Composable
private fun ExploreSectionError(
    message: String,
    onRetryClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 22.dp,
                    vertical = 20.dp,
                ),
        horizontalAlignment =
            Alignment.CenterHorizontally,
    ) {
        Text(
            text =
            message,
            style =
                LocalAppTypography
                    .current
                    .bodySmall
                    .medium
                    .copy(
                        color =
                        Natural60,
                    ),
        )

        TextButton(
            onClick =
            onRetryClick,
        ) {
            Text(
                text =
                    "다시 시도",
                style =
                    LocalAppTypography
                        .current
                        .bodySmall
                        .bold
                        .copy(
                            color =
                            Primary,
                        ),
            )
        }
    }
}
