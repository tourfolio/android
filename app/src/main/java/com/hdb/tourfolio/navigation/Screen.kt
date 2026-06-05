package com.hdb.tourfolio.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Tab1 : Screen("tab1")

    data object Trade : Screen("trade")

    data object Home : Screen("home")

    data object Tab2 : Screen("tab2")

    data object Tab3 : Screen("tab3")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
)

val bottomNavItems =
    listOf(
        BottomNavItem(Screen.Tab1, "탭1", Icons.Outlined.Search),
        BottomNavItem(Screen.Trade, "주식", Icons.Outlined.Menu),
        BottomNavItem(Screen.Home, "홈", Icons.Outlined.Home),
        BottomNavItem(Screen.Tab2, "탭2", Icons.Outlined.Star),
        BottomNavItem(Screen.Tab3, "탭3", Icons.Outlined.Person),
    )
