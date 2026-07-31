package com.hdb.tourfolio.feature.explore.model

import androidx.annotation.DrawableRes

data class CityTravelDetailUiModel(
    val id: Long,
    val categoryTitle: String,
    val title: String,
    val placeCount: Int,
    @DrawableRes val cityImageRes: Int,
    val spots: List<CityTravelSpotUiModel>,
)

data class CityTravelSpotUiModel(
    val id: Long,
    val title: String,
    @DrawableRes val imageRes: Int,
)
