package com.hdb.tourfolio.navigation

import androidx.annotation.DrawableRes
import com.hdb.tourfolio.R

sealed class Screen(val route: String) {
    data object ExploreCarousel : Screen("explore_carousel")

    data object Explore : Screen("explore")

    data object Trade : Screen("trade")

    data object Home : Screen("home")

    data object Quest : Screen("quest")

    data object Card : Screen("card")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    @param:DrawableRes val selectedIcon: Int,
    @param:DrawableRes val unselectedIcon: Int,
)

val bottomNavItems =
    listOf(
        BottomNavItem(Screen.ExploreCarousel, "탐색", R.drawable.ic_explore_filled, R.drawable.ic_explore_outlined),
        BottomNavItem(Screen.Trade, "투자", R.drawable.ic_trade_filled, R.drawable.ic_trade_outlined),
        BottomNavItem(Screen.Home, "홈", R.drawable.ic_home_filled, R.drawable.ic_home_outlined),
        BottomNavItem(Screen.Quest, "업적", R.drawable.ic_quest_filled, R.drawable.ic_quest_outlined),
        BottomNavItem(Screen.Card, "수집", R.drawable.ic_card_filled, R.drawable.ic_card_outlined),
    )
