@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.hdb.tourfolio.feature.explore.presentation.components.TourSpotCard
import com.hdb.tourfolio.feature.home.presentation.components.HomeCardCollectionCard
import com.hdb.tourfolio.feature.home.presentation.components.HomePortfolioCard
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.home.presentation.components.PointBalanceCard
import com.hdb.tourfolio.ui.components.CommonHeader
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onTourSpotClick: (Long) -> Unit,
    onPortfolioClick: () -> Unit,
    onCardCollectionClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by
        viewModel.state
            .collectAsStateWithLifecycle()

    LifecycleResumeEffect(viewModel) {
        viewModel.processIntent(HomeIntent.FetchHome)
        onPauseOrDispose {}
    }

    when (
        val homeState =
            state.homeState
    ) {
        HomeRequestState.Loading -> {
            HomeLoading(
                modifier = modifier,
            )
        }

        is HomeRequestState.Error -> {
            HomeError(
                message =
                    homeState.message,
                onRetryClick = {
                    viewModel.processIntent(
                        HomeIntent.FetchHome,
                    )
                },
                modifier =
                modifier,
            )
        }

        is HomeRequestState.Success -> {
            HomeContent(
                portfolio =
                    homeState.home.portfolio,
                cardCollection =
                    homeState.home.cardCollection,
                recommendedSpots =
                    homeState.home.recommendedSpots,
                onProfileClick =
                onProfileClick,
                onNotificationClick =
                onNotificationClick,
                onTourSpotClick =
                onTourSpotClick,
                onPortfolioClick = onPortfolioClick,
                onCardCollectionClick = onCardCollectionClick,
                modifier =
                modifier,
            )
        }
    }
}

@Composable
private fun HomeContent(
    portfolio: com.hdb.tourfolio.domain.home.model.HomePortfolio,
    cardCollection: com.hdb.tourfolio.domain.home.model.HomeCardCollection,
    recommendedSpots: List<com.hdb.tourfolio.domain.home.model.HomeRecommendedSpot>,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onTourSpotClick: (Long) -> Unit,
    onPortfolioClick: () -> Unit,
    onCardCollectionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Natural100,
                ),
    ) {
        /*
         * Hero + 포트폴리오
         */
        item {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            580.dp,
                        ),
            ) {
                /*
                 * 홈 Hero 배경
                 */
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                540.dp,
                            ),
                ) {
                    Image(
                        painter =
                            painterResource(
                                id =
                                    R.drawable.bg_home_hero,
                            ),
                        contentDescription =
                            "홈 배경",
                        modifier =
                            Modifier.fillMaxSize(),
                        contentScale =
                            ContentScale.Crop,
                    )

                    CommonHeader(
                        onProfileClick =
                        onProfileClick,
                        onNotificationClick =
                        onNotificationClick,
                        contentColor =
                        Natural100,
                        modifier =
                            Modifier
                                .align(
                                    Alignment.TopCenter,
                                )
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 18.dp,
                                ),
                    )

                    Column(
                        modifier =
                            Modifier
                                .align(
                                    Alignment.TopStart,
                                )
                                .padding(
                                    start = 22.dp,
                                    top = 110.dp,
                                    end = 22.dp,
                                ),
                    ) {
                        Text(
                            text =
                                "새로운 여행지에\n투자를 해볼까요?",
                            style =
                                LocalAppTypography
                                    .current
                                    .titleLarge
                                    .copy(
                                        color =
                                        Natural100,
                                    ),
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    10.dp,
                                ),
                        )

                        Text(
                            text =
                                "당신의 다음 여행이 기다리고 있어요",
                            style =
                                LocalAppTypography
                                    .current
                                    .bodyLarge
                                    .medium
                                    .copy(
                                        color =
                                            Natural100.copy(
                                                alpha = 0.9f,
                                            ),
                                    ),
                        )
                    }
                }

                /*
                 * Hero 배경 위에 겹쳐지는 포트폴리오 카드
                 */
                HomePortfolioCard(
                    totalAsset =
                        portfolio.totalAsset,
                    todayProfit =
                        portfolio.todayProfit,
                    todayProfitRate =
                        portfolio.todayProfitRate,
                    totalProfitRate =
                        portfolio.totalProfitRate,
                    stockCount =
                        portfolio.stockCount,
                    modifier =
                        Modifier
                            .align(
                                Alignment.BottomCenter,
                            )
                            .padding(
                                horizontal = 22.dp,
                            )
                            .clickable(onClick = onPortfolioClick),
                )
            }
        }

        /*
         * 보유 포인트
         */
        item {
            Spacer(
                modifier =
                    Modifier.height(
                        10.dp,
                    ),
            )

            PointBalanceCard(
                pointBalance =
                    portfolio.pointBalance,
                modifier =
                    Modifier.padding(
                        horizontal = 22.dp,
                    ),
            )
        }

        /*
         * 임시 축제/공지 이미지
         */
        item {
            Spacer(
                modifier =
                    Modifier.height(
                        28.dp,
                    ),
            )

            Image(
                painter =
                    painterResource(
                        id =
                            R.drawable.img_home_notice,
                    ),
                contentDescription =
                    "축제 안내",
                modifier =
                    Modifier
                        .padding(
                            horizontal =
                                22.dp,
                        )
                        .fillMaxWidth()
                        .height(
                            190.dp,
                        )
                        .clip(
                            RoundedCornerShape(
                                10.dp,
                            ),
                        ),
                contentScale =
                    ContentScale.Crop,
            )
        }

        /*
         * 카드 수집
         */
        item {
            Spacer(
                modifier =
                    Modifier.height(
                        28.dp,
                    ),
            )

            HomeCardCollectionCard(
                ownedCount =
                    cardCollection.ownedCount,
                totalCount =
                    cardCollection.totalCount,
                collectionRate =
                    cardCollection.collectionRate,
                modifier =
                    Modifier.padding(
                        horizontal =
                            22.dp,
                    ).clickable(onClick = onCardCollectionClick),
            )
        }

        /*
         * 추천 관광지
         */
        item {
            Spacer(
                modifier =
                    Modifier.height(
                        38.dp,
                    ),
            )

            Text(
                text = "이번주 추천 관광지",
                style =
                    LocalAppTypography
                        .current
                        .titleMedium
                        .bold
                        .copy(
                            color =
                            Natural10,
                        ),
                modifier =
                    Modifier.padding(
                        horizontal =
                            22.dp,
                    ),
            )

            Spacer(
                modifier =
                    Modifier.height(
                        18.dp,
                    ),
            )
        }

        item {
            LazyRow(
                contentPadding =
                    androidx.compose.foundation.layout.PaddingValues(
                        start = 22.dp,
                        end = 22.dp,
                    ),
                horizontalArrangement =
                    androidx.compose.foundation.layout.Arrangement.spacedBy(
                        12.dp,
                    ),
            ) {
                items(
                    items =
                    recommendedSpots,
                    key = { spot ->
                        spot.id
                    },
                ) { spot ->
                    TourSpotCard(
                        id =
                            spot.id,
                        title =
                            spot.title,
                        content =
                            spot.description,
                        tags =
                            spot.tags,
                        imageUrl =
                            spot.imageUrl,
                        hasImage = spot.hasImage,
                        onClick =
                        onTourSpotClick,
                        modifier =
                            Modifier.width(
                                330.dp,
                            ),
                    )
                }
            }
        }

        item {
            Spacer(
                modifier =
                    Modifier.height(
                        34.dp,
                    ),
            )
        }
    }
}

@Composable
private fun HomeLoading(modifier: Modifier = Modifier) {
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
            color = Primary,
        )
    }
}

@Composable
private fun HomeError(
    message: String,
    onRetryClick: () -> Unit,
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
                    "홈 정보를 불러오지 못했습니다.",
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
                        8.dp,
                    ),
            )

            Text(
                text = message,
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
}
