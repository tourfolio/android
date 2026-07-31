package com.hdb.tourfolio.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hdb.tourfolio.feature.explore.CityTravelDetailScreen
import com.hdb.tourfolio.feature.explore.ExploreDetailScreen
import com.hdb.tourfolio.feature.explore.ExploreEntryScreen
import com.hdb.tourfolio.feature.explore.ExploreSearchScreen
import com.hdb.tourfolio.feature.explore.mock.TourSpotMockData
import com.hdb.tourfolio.feature.trade.TradeScreen
import com.hdb.tourfolio.navigation.Screen.ExploreDetail.ARG_TOUR_SPOT_ID

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    /*
     * 하단 내비게이션의 메인 화면 라우트에서만 BottomNavBar를 표시(?)
     */
    val showBottomBar =
        bottomNavItems.any { bottomNavItem ->
            bottomNavItem.screen.route == currentRoute
        }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navController = navController,
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
            composable(Screen.Explore.route) {
                ExploreEntryScreen(
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
            composable(Screen.ExploreSearch.route) {
                ExploreSearchScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onTourSpotClick = { tourSpotId ->
                        navController.navigate(
                            Screen.ExploreDetail.createRoute(
                                tourSpotId = tourSpotId,
                            ),
                        )
                    },
                )
            }

            /*
             * 탐색 상세 화면
             */
            composable(
                route = Screen.ExploreDetail.route,
                arguments =
                    listOf(
                        navArgument(Screen.ExploreDetail.ARG_TOUR_SPOT_ID) {
                            type = NavType.LongType
                        },
                    ),
            ) { backStackEntry ->
                val tourSpotId =
                    backStackEntry.arguments
                        ?.getLong(Screen.ExploreDetail.ARG_TOUR_SPOT_ID)
                        ?: return@composable

                ExploreDetailScreen(
                    tourSpotId = tourSpotId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onShareClick = {
                        // 추후 공유 기능 연결
                    },
                    // 주변 관광지 상세 클릭 - 추후 의논 후 제거
                    onNearbySpotClick = { nearbyTourSpotId ->
                        if (
                            TourSpotMockData.findDetailById(
                                nearbyTourSpotId,
                            ) != null
                        ) {
                            navController.navigate(
                                Screen.ExploreDetail.createRoute(
                                    tourSpotId = nearbyTourSpotId,
                                ),
                            )
                        }
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
                TradeScreen()
            }

            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.Quest.route) {
                QuestScreen()
            }

            composable(Screen.Card.route) {
                CardScreen()
            }
        }
    }
}

// TODO: 실제 화면 구성 후 제거

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "홈")
    }
}

@Composable
fun QuestScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "업적")
    }
}

@Composable
fun CardScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "수집")
    }
}