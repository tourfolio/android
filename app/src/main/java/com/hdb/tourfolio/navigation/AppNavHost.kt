package com.hdb.tourfolio.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hdb.tourfolio.feature.card.presentation.CardScreen
import com.hdb.tourfolio.feature.explore.presentation.CityTravelDetailScreen
import com.hdb.tourfolio.feature.explore.presentation.ExploreDetailScreen
import com.hdb.tourfolio.feature.explore.presentation.ExploreEntryScreen
import com.hdb.tourfolio.feature.explore.presentation.ExploreSearchScreen
import com.hdb.tourfolio.feature.home.presentation.HomeScreen
import com.hdb.tourfolio.feature.trade.presentation.TradeScreen
import com.hdb.tourfolio.feature.trade.presentation.detail.StockDetailScreen

private const val EXPLORE_INTRO_FINISHED_KEY =
    "explore_intro_finished"

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var hideBottomBar by rememberSaveable {
        mutableStateOf(false)
    }

    val showBottomBar =
        !hideBottomBar &&
            bottomNavItems.any { bottomNavItem ->
                bottomNavItem.screen.route == currentRoute
            }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter =
                    slideInVertically(
                        initialOffsetY = { fullHeight ->
                            fullHeight
                        },
                        animationSpec =
                            tween(
                                durationMillis = 220,
                            ),
                    ) +
                        fadeIn(
                            animationSpec =
                                tween(
                                    durationMillis = 180,
                                ),
                        ),
                exit =
                    slideOutVertically(
                        targetOffsetY = { fullHeight ->
                            fullHeight
                        },
                        animationSpec =
                            tween(
                                durationMillis = 220,
                            ),
                    ) +
                        fadeOut(
                            animationSpec =
                                tween(
                                    durationMillis = 150,
                                ),
                        ),
            ) {
                BottomNavBar(
                    navController = navController,
                    onDestinationSelected = { screen ->

                        hideBottomBar = false

                        if (screen == Screen.Explore) {
                            navController
                                .getBackStackEntry(Screen.Explore.route)
                                .savedStateHandle[EXPLORE_INTRO_FINISHED_KEY] = false
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            /*
             * 탐색 진입 화면
             */
            composable(
                route = Screen.Explore.route,
            ) { exploreBackStackEntry ->
                val introFinished by
                    exploreBackStackEntry.savedStateHandle
                        .getStateFlow(
                            key = EXPLORE_INTRO_FINISHED_KEY,
                            initialValue = false,
                        )
                        .collectAsState()

                ExploreEntryScreen(
                    introFinished = introFinished,
                    onIntroFinished = {
                        exploreBackStackEntry.savedStateHandle[
                            EXPLORE_INTRO_FINISHED_KEY,
                        ] = true
                    },
                    onIntroTourSpotClick = { tourSpotId ->
                        navController.navigate(
                            Screen.ExploreDetail.createRoute(
                                tourSpotId = tourSpotId,
                                fromIntro = true,
                            ),
                        )
                    },
                    onCityTravelClick = { travelId ->
                        navController.navigate(
                            Screen.CityTravelDetail.createRoute(
                                travelId = travelId,
                            ),
                        )
                    },
                    onTourSpotClick = { tourSpotId ->
                        navController.navigate(
                            Screen.ExploreDetail.createRoute(
                                tourSpotId = tourSpotId,
                                fromIntro = false,
                            ),
                        )
                    },
                    onSearchClick = {
                        navController.navigate(
                            Screen.ExploreSearch.route,
                        )
                    },
                )
            }

            /*
             * 탐색 검색 화면
             */
            composable(
                route = Screen.ExploreSearch.route,
            ) {
                ExploreSearchScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onTourSpotClick = { tourSpotId ->
                        navController.navigate(
                            Screen.ExploreDetail.createRoute(
                                tourSpotId = tourSpotId,
                                fromIntro = false,
                            ),
                        )
                    },
                )
            }

            /*
             * 관광지 상세 화면
             */
            composable(
                route = Screen.ExploreDetail.route,
                arguments =
                    listOf(
                        navArgument(
                            Screen.ExploreDetail.ARG_TOUR_SPOT_ID,
                        ) {
                            type = NavType.LongType
                        },
                        navArgument(
                            Screen.ExploreDetail.ARG_FROM_INTRO,
                        ) {
                            type = NavType.BoolType
                            defaultValue = false
                        },
                    ),
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(220),
                    )
                },
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(180),
                    )
                },
            ) { detailBackStackEntry ->
                val tourSpotId =
                    detailBackStackEntry.arguments
                        ?.getLong(
                            Screen.ExploreDetail.ARG_TOUR_SPOT_ID,
                        )
                        ?: return@composable

                val fromIntro =
                    detailBackStackEntry.arguments
                        ?.getBoolean(
                            Screen.ExploreDetail.ARG_FROM_INTRO,
                        )
                        ?: false

                ExploreDetailScreen(
                    tourSpotId = tourSpotId,
                    onBackClick = {
                        if (fromIntro) {
                            navController
                                .getBackStackEntry(
                                    Screen.Explore.route,
                                )
                                .savedStateHandle[
                                EXPLORE_INTRO_FINISHED_KEY,
                            ] = true
                        }

                        navController.popBackStack()
                    },
                    onShareClick = {
                        // 추후 공유 기능 연결
                    },
                )
            }

            /*
             * 도시별 추천여행 상세 화면
             */
            composable(
                route = Screen.CityTravelDetail.route,
                arguments =
                    listOf(
                        navArgument(Screen.CityTravelDetail.ARG_TRAVEL_ID) {
                            type = NavType.LongType
                        },
                    ),
            ) { backStackEntry ->
                val travelId =
                    backStackEntry.arguments
                        ?.getLong(Screen.CityTravelDetail.ARG_TRAVEL_ID)
                        ?: return@composable

                CityTravelDetailScreen(
                    travelId = travelId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onShareClick = {
                        // 추후 연결
                    },
                    onSpotClick = { spotId ->
                        // 추후 의논 후 제거 혹은 구현
                    },
                )
            }

            composable(Screen.Trade.route) {
                TradeScreen(
                    onSearchClick = {
                        // 추후 검색 화면 연결
                    },
                    onNotificationClick = {
                        // 추후 알림 화면 연결
                    },
                    onStockClick = { stockId, stockName, currentPrice, prevPrice ->
                        navController.navigate(
                            Screen.StockDetail.createRoute(stockId, stockName, currentPrice, prevPrice),
                        )
                    },
                )
            }

            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.Quest.route) {
                QuestScreen()
            }

            /*
             * 수집
             */
            composable(
                route = Screen.Card.route,
            ) {
                CardScreen(
                    onProfileClick = {
                        // 추후 프로필 연결
                    },
                    onNotificationClick = {
                        // 추후 알림 연결
                    },
                    onExpandedImageVisibilityChange = { isExpanded ->
                        hideBottomBar =
                            isExpanded
                    },
                )
            }

            composable(
                route = Screen.StockDetail.route,
                arguments =
                    listOf(
                        navArgument("stockId") { type = NavType.LongType },
                        navArgument("stockName") { type = NavType.StringType },
                        navArgument("currentPrice") {
                            type = NavType.StringType
                            nullable = true
                        },
                        navArgument("prevPrice") {
                            type = NavType.StringType
                            nullable = true
                        },
                    ),
            ) { backStackEntry ->
                val stockId = backStackEntry.arguments?.getLong("stockId") ?: 0L
                val stockName =
                    backStackEntry.arguments
                        ?.getString("stockName")
                        ?.let { Uri.decode(it) }
                        ?: ""
                val currentPrice =
                    backStackEntry.arguments
                        ?.getString("currentPrice")
                        ?.toLongOrNull()
                val prevPrice =
                    backStackEntry.arguments
                        ?.getString("prevPrice")
                        ?.toLongOrNull()

                StockDetailScreen(
                    stockId = stockId,
                    stockName = stockName,
                    initialCurrentPrice = currentPrice,
                    initialPrevPrice = prevPrice,
                    onBackClick = { navController.popBackStack() },
                )
            }
        }
    }
}

// TODO: 실제 화면 구성 후 제거

@Composable
fun QuestScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "업적")
    }
}
