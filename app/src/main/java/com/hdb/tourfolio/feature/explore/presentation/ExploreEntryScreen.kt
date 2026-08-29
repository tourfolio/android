package com.hdb.tourfolio.feature.explore.presentation

import androidx.compose.runtime.Composable

@Composable
fun ExploreEntryScreen(
    introFinished: Boolean,
    onIntroFinished: () -> Unit,
    onIntroTourSpotClick: (Long) -> Unit,
    onCityTravelClick: (Long) -> Unit,
    onTourSpotClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
) {
    if (introFinished) {
        ExploreScreen(
            onCityTravelClick = onCityTravelClick,
            onTourSpotClick = onTourSpotClick,
            onSearchClick = onSearchClick,
            onProfileClick = onProfileClick,
            onNotificationClick = onNotificationClick,
        )
    } else {
        ExploreCarouselScreen(
            onFinished = onIntroFinished,
            onTourSpotClick = onIntroTourSpotClick,
        )
    }
}
