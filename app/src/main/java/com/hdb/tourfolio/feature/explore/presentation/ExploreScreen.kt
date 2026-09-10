@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.presentation.components.PlaceCard
import com.hdb.tourfolio.feature.explore.presentation.components.RegionCard
import com.hdb.tourfolio.feature.explore.presentation.components.SearchBar
import com.hdb.tourfolio.feature.explore.presentation.components.TourSpotCard
import com.hdb.tourfolio.feature.explore.presentation.components.TourSpotCarousel
import com.hdb.tourfolio.ui.components.CommonHeader
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary

@Composable
fun ExploreScreen(
    onCollectionClick: (Long) -> Unit = {},
    onTourSpotClick: (Long) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ExploreHomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp),
    ) {
        CommonHeader(
            onProfileClick = onProfileClick,
            onNotificationClick = onNotificationClick,
            modifier =
                Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 18.dp,
                ),
        )

        Spacer(modifier = Modifier.height(10.dp))

        SearchBar(
            value = "",
            onValueChange = {},
            readOnly = true,
            onClick = onSearchClick,
            modifier = Modifier.padding(horizontal = 22.dp),
        )

        Spacer(modifier = Modifier.height(18.dp))

        when (val exploreCards = state.exploreCards) {
            ExploreCardsUiState.Loading -> {
                ExploreSectionLoading(height = 370.dp)
            }

            is ExploreCardsUiState.Error -> {
                ExploreSectionError(
                    message = exploreCards.message,
                    onRetryClick = { viewModel.processIntent(ExploreHomeIntent.FetchExploreCards) },
                )
            }

            is ExploreCardsUiState.Success -> {
                TourSpotCarousel(
                    items = exploreCards.featuredCards,
                    onItemClick = onTourSpotClick,
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        ExploreSectionTitle(
            title =
                "투어 컬렉션 모음",
            modifier =
                Modifier.padding(
                    horizontal = 22.dp,
                ),
        )

        Spacer(
            modifier =
                Modifier.height(
                    16.dp,
                ),
        )

        when (
            val collections =
                state.collections
        ) {
            ExploreCollectionsUiState.Loading -> {
                ExploreSectionLoading(
                    height = 280.dp,
                )
            }

            is ExploreCollectionsUiState.Error -> {
                ExploreSectionError(
                    message =
                        collections.message,
                    onRetryClick = {
                        viewModel.processIntent(
                            ExploreHomeIntent.FetchCollections,
                        )
                    },
                )
            }

            is ExploreCollectionsUiState.Success -> {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 22.dp,
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            17.dp,
                        ),
                ) {
                    collections.collections.forEach { collection ->
                        PlaceCard(
                            title =
                                collection.title,
                            places =
                                collection.placeCount,
                            imageUrl =
                                collection.thumbnailUrl,
                            onClick = {
                                onCollectionClick(
                                    collection.id,
                                )
                            },
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(34.dp))

        ExploreSectionTitle(
            title = "놓치면 아쉬운 추천 명소",
            modifier = Modifier.padding(horizontal = 22.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val exploreCards = state.exploreCards) {
            ExploreCardsUiState.Loading -> {
                ExploreSectionLoading(height = 220.dp)
            }

            is ExploreCardsUiState.Error -> {
                ExploreSectionError(
                    message = exploreCards.message,
                    onRetryClick = { viewModel.processIntent(ExploreHomeIntent.FetchExploreCards) },
                )
            }

            is ExploreCardsUiState.Success -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 22.dp),
                ) {
                    items(items = exploreCards.recommendedCards, key = { item -> item.id }) { item ->
                        TourSpotCard(
                            id = item.id,
                            title = item.title,
                            content = item.description,
                            tags = item.tags,
                            imageUrl = item.imageUrl,
                            hasImage = item.hasImage,
                            onClick = onTourSpotClick,
                            modifier = Modifier.width(360.dp),
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

        when (val trending = state.trending) {
            ExploreTrendingUiState.Loading -> {
                ExploreSectionLoading(height = 245.dp)
            }

            is ExploreTrendingUiState.Error -> {
                ExploreSectionError(
                    message = trending.message,
                    onRetryClick = { viewModel.processIntent(ExploreHomeIntent.FetchTrendingCards) },
                )
            }

            is ExploreTrendingUiState.Success -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 22.dp),
                ) {
                    items(items = trending.cards, key = { item -> item.id }) { item ->
                        RegionCard(
                            id = item.id,
                            title = item.title,
                            regionName = item.areaName,
                            imageUrl = item.imageUrl,
                            hasImage = item.hasImage,
                            onClick = onTourSpotClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    showMoreIcon: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = LocalAppTypography.current.titleMedium.bold.copy(color = Natural10),
        )

        if (showMoreIcon) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_down_gray),
                contentDescription = "더보기",
                modifier = Modifier.size(16.dp).rotate(-90f),
            )
        }
    }
}

@Composable
private fun ExploreSectionLoading(height: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier.fillMaxWidth().height(height),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun ExploreSectionError(
    message: String,
    onRetryClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = LocalAppTypography.current.bodySmall.medium.copy(color = Natural60),
        )

        TextButton(onClick = onRetryClick) {
            Text(
                text = "다시 시도",
                style = LocalAppTypography.current.bodySmall.bold.copy(color = Primary),
            )
        }
    }
}
