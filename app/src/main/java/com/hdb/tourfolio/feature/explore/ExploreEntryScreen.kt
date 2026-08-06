package com.hdb.tourfolio.feature.explore

import androidx.compose.runtime.Composable

@Composable
fun ExploreEntryScreen(
    introFinished: Boolean,
    onIntroFinished: () -> Unit,
    onIntroTourSpotClick: (Long) -> Unit,
    onCityTravelClick: (Long) -> Unit,
    onTourSpotClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
) {
    if (introFinished) {
        ExploreScreen(
            onCityTravelClick = onCityTravelClick,
            onTourSpotClick = onTourSpotClick,
            onSearchClick = onSearchClick,
        )
    } else {
        ExploreCarouselScreen(
            onFinished = onIntroFinished,
            onTourSpotClick = onIntroTourSpotClick,
        )
    }
}
