package com.hdb.tourfolio.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hdb.tourfolio.ui.search.SearchScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            composable(Screen.Search.route) { SearchScreen() }
            composable(Screen.Trade.route) { TradeScreen() }
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Tab2.route) { SampleScreen() }
            composable(Screen.Tab3.route) { SampleScreen() }
        }
    }
}

// todo: 화면 구성 후 제거
@Composable
fun SampleScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("샘플")
    }
}

@Composable
fun TradeScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("주식")
    }
}

@Composable
fun HomeScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("홈")
    }
}
