package com.hdb.tourfolio.navigation

import android.net.Uri
import androidx.annotation.DrawableRes
import com.hdb.tourfolio.R

sealed class Screen(val route: String) {
    data object Explore : Screen("explore")

    data object ExploreSearch : Screen("explore_search")

    data object Trade : Screen("trade")

    data object Home : Screen("home")

    data object Mission : Screen("mission")

    data object MonthlyAttendance : Screen("monthly_attendance")

    data object Card : Screen("card")

    data object MyPage : Screen("my_page")

    data object Notification : Screen("notification")

    data object CityTravelDetail :
        Screen("city_travel_detail/{travelId}") {
        const val ARG_TRAVEL_ID = "travelId"

        fun createRoute(travelId: Long): String = "city_travel_detail/$travelId"
    }

    data object ExploreDetail :
        Screen(
            "explore_detail/{tourSpotId}?fromIntro={fromIntro}",
        ) {
        const val ARG_TOUR_SPOT_ID = "tourSpotId"
        const val ARG_FROM_INTRO = "fromIntro"

        fun createRoute(
            tourSpotId: Long,
            fromIntro: Boolean = false,
        ): String = "explore_detail/$tourSpotId?fromIntro=$fromIntro"
    }

    data object StockDetail :
        Screen("stock_detail/{stockId}/{stockName}?currentPrice={currentPrice}&prevPrice={prevPrice}") {
        fun createRoute(
            stockId: Long,
            stockName: String,
            currentPrice: Long? = null,
            prevPrice: Long? = null,
        ): String {
            val base = "stock_detail/$stockId/${Uri.encode(stockName)}"
            return "$base?currentPrice=${currentPrice ?: ""}&prevPrice=${prevPrice ?: ""}"
        }
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    @param:DrawableRes val selectedIcon: Int,
    @param:DrawableRes val unselectedIcon: Int,
)

val bottomNavItems =
    listOf(
        BottomNavItem(Screen.Explore, "탐색", R.drawable.ic_explore_filled, R.drawable.ic_explore_outlined),
        BottomNavItem(Screen.Trade, "투자", R.drawable.ic_trade_filled, R.drawable.ic_trade_outlined),
        BottomNavItem(Screen.Home, "홈", R.drawable.ic_home_filled, R.drawable.ic_home_outlined),
        BottomNavItem(Screen.Mission, "업적", R.drawable.ic_mission_filled, R.drawable.ic_mission_outlined),
        BottomNavItem(Screen.Card, "수집", R.drawable.ic_card_filled, R.drawable.ic_card_outlined),
    )
