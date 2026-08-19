@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = "홈",
            style = LocalAppTypography.current.titleMedium.bold,
            color = Natural10,
        )
    }
}
