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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.presentation.components.CollectionSpotCard
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreCollectionDetailUiModel
import com.hdb.tourfolio.ui.components.CommonBackHeader
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary

@Composable
fun CollectionDetailScreen(
    collectionId: Long,
    onBackClick: () -> Unit,
    onSpotClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CollectionDetailViewModel =
        hiltViewModel(),
) {
    val state by
        viewModel.state
            .collectAsStateWithLifecycle()

    LaunchedEffect(
        collectionId,
    ) {
        viewModel.processIntent(
            CollectionDetailIntent.FetchCollectionDetail(
                collectionId =
                collectionId,
            ),
        )
    }

    when (
        val detailState =
            state.detail
    ) {
        CollectionDetailUiState.Loading -> {
            CollectionDetailLoading(
                modifier =
                modifier,
            )
        }

        is CollectionDetailUiState.Error -> {
            CollectionDetailError(
                message =
                    detailState.message,
                onBackClick =
                onBackClick,
                modifier =
                modifier,
            )
        }

        is CollectionDetailUiState.Success -> {
            CollectionDetailContent(
                detail =
                    detailState.detail,
                onBackClick =
                onBackClick,
                onSpotClick =
                onSpotClick,
                modifier =
                modifier,
            )
        }
    }
}

@Composable
private fun CollectionDetailContent(
    detail: ExploreCollectionDetailUiModel,
    onBackClick: () -> Unit,
    onSpotClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Natural100,
                ),
    ) {
        /*
         * 상단 헤더
         */
        CommonBackHeader(
            title =
                detail.title,
            onBackClick =
            onBackClick,
        )

        LazyVerticalGrid(
            columns =
                GridCells.Fixed(
                    2,
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(
                        1f,
                    ),
            horizontalArrangement =
                Arrangement.spacedBy(
                    10.dp,
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    10.dp,
                ),
            contentPadding =
                PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 32.dp,
                ),
        ) {
            /*
             * 관광지 개수
             */
            item(
                span = {
                    GridItemSpan(
                        maxLineSpan,
                    )
                },
            ) {
                CollectionPlaceCount(
                    placeCount =
                        detail.placeCount,
                )
            }

            item(
                span = {
                    GridItemSpan(
                        maxLineSpan,
                    )
                },
            ) {
            }

            items(
                items =
                    detail.spots,
                key = { spot ->
                    spot.id
                },
            ) { spot ->
                CollectionSpotCard(
                    title =
                        spot.title,
                    imageUrl =
                        spot.imageUrl,
                    onClick = {
                        onSpotClick(
                            spot.id,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun CollectionPlaceCount(
    placeCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(
                7.dp,
            ),
    ) {
        Image(
            painter =
                painterResource(
                    id =
                        R.drawable.ic_location,
                ),
            contentDescription =
            null,
            modifier =
                Modifier.size(
                    26.dp,
                ),
        )

        Text(
            text =
                "$placeCount places",
            style =
                LocalAppTypography
                    .current
                    .bodyLarge
                    .medium
                    .copy(
                        color =
                        Natural60,
                    ),
        )
    }
}

@Composable
private fun CollectionDetailLoading(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Natural100,
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
private fun CollectionDetailError(
    message: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Natural100,
                ),
        contentAlignment =
            Alignment.Center,
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,
        ) {
            Text(
                text =
                message,
                style =
                    LocalAppTypography
                        .current
                        .bodyLarge
                        .bold
                        .copy(
                            color =
                            Natural10,
                        ),
            )

            Spacer(
                modifier =
                    Modifier.height(
                        12.dp,
                    ),
            )

            TextButton(
                onClick =
                onBackClick,
            ) {
                Text(
                    text =
                        "뒤로 가기",
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
}
