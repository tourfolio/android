package com.hdb.tourfolio.feature.explore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun ExploreEntryScreen(
    onCityTravelClick: (Long) -> Unit,
) {
    var introFinished by rememberSaveable {
        mutableStateOf(false)
    }

    if (introFinished) {
        ExploreScreen(
            onCityTravelClick = onCityTravelClick,
        )
    } else {
        ExploreCarouselScreen(
            onFinished = {
                introFinished = true
            },
        )
    }
}