package com.hdb.tourfolio.domain.explore.model

import com.hdb.tourfolio.domain.common.model.ThemeType

data class ExploreMainCard(
    val spotId: Long,
    val name: String,
    val subTitle: String,
    val description: String,
    val location: String,
    val address: String,
    val imageUrl: String,
    val themeType: ThemeType,
    val tags: List<String>,
    val totalCount: Int,
    val currentIndex: Int,
)

data class ExploreCard(
    val id: Long,
    val name: String,
    val areaCode: String,
    val areaName: String,
    val themeTag: String,
    val tier: Int,
    val imageUrl: String,
    val description: String,
    val mapX: String,
    val mapY: String,
    val address: String,
    val tags: List<String>,
)

data class ExploreHub(
    val themes: List<ExploreHubTheme>,
    val trendingSpots: List<ExploreHubTrendingSpot>,
)

data class ExploreHubTheme(
    val themeId: Long,
    val title: String,
    val placeCount: Int,
    val imageUrl: String,
)

data class ExploreHubTrendingSpot(
    val spotId: Long,
    val name: String,
    val location: String,
    val popularityRank: Int,
    val imageUrl: String,
    val address: String,
)

data class ExploreSearchResult(
    val spots: List<ExploreSearchSpot>,
    val totalCount: Int,
)

data class ExploreSearchSpot(
    val spotId: Long,
    val name: String,
    val location: String,
    val address: String,
    val imageUrl: String,
    val tags: List<String>,
)

data class ExploreSpotDetail(
    val spotId: Long,
    val name: String,
    val address: String,
    val tags: List<String>,
    val description: String,
    val operatingHours: String?,
    val closedDays: String?,
    val admissionFee: String?,
    val website: String?,
    val phoneNumber: String?,
    val attractionPoints: List<ExploreAttractionPoint>,
    val nearbySpots: List<ExploreNearbySpot>,
)

data class ExploreAttractionPoint(
    val title: String,
    val iconType: String,
    val iconUrl: String?,
)

data class ExploreNearbySpot(
    val spotId: Long,
    val name: String,
    val imageUrl: String,
)

data class CityTravelDetail(
    val id: Long,
    val categoryTitle: String,
    val title: String,
    val placeCount: Int,
    val cityImageKey: String,
    val spots: List<CityTravelSpot>,
)

data class CityTravelSpot(
    val id: Long,
    val title: String,
    val imageKey: String,
)
