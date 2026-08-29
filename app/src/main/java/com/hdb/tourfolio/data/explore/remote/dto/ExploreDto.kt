package com.hdb.tourfolio.data.explore.remote.dto

/*
 * 탐색 첫 진입 시 카드
 */
data class ExploreMainCardDto(
    val spotId: Long,
    val name: String,
    val subTitle: String?,
    val description: String?,
    val location: String,
    val address: String,
    val imageUrl: String,
    val theme: String,
    val tags: List<String>,
    val totalCount: Int,
    val currentIndex: Int,
)

/*
 * 탐색 일반 관광지 카드
 */
data class ExploreCardDto(
    val id: Long,
    val name: String,
    val areaCode: String,
    val areaName: String,
    val themeTag: String,
    val tier: Int,
    val imageUrl: String,
    val description: String?,
    val mapX: String,
    val mapY: String,
    val address: String,
    val tags: List<String>,
)

/*
 * 콘텐츠 허브
 */

data class ExploreHubDto(
    val themes: List<ExploreHubThemeDto>,
    val trendingSpots: List<ExploreHubTrendingSpotDto>,
)

data class ExploreHubThemeDto(
    val themeId: Long,
    val title: String,
    val placeCount: Int,
    val imageUrl: String,
)

data class ExploreHubTrendingSpotDto(
    val spotId: Long,
    val name: String,
    val location: String,
    val popularityRank: Int,
    val imageUrl: String,
    val address: String,
)

/*
 * 복합 필터링 검색
 */
data class ExploreSearchDto(
    val spots: List<ExploreSearchSpotDto>,
    val totalCount: Int,
)

data class ExploreSearchSpotDto(
    val spotId: Long,
    val name: String,
    val location: String,
    val address: String,
    val imageUrl: String,
    val tags: List<String>,
)

data class ExploreSpotDetailDto(
    val spotId: Long,
    val name: String,
    val imageUrl: String,
    val address: String,
    val tags: List<String>,
    val description: String?,
    val operatingHours: String?,
    val closedDays: String?,
    val admissionFee: String?,
    val website: String?,
    val phoneNumber: String?,
    val attractionPoints: List<ExploreAttractionPointDto>,
    val nearbySpots: List<ExploreNearbySpotDto>,
)

data class ExploreAttractionPointDto(
    val title: String,
    val iconType: String,
    val iconUrl: String?,
)

data class ExploreNearbySpotDto(
    val spotId: Long,
    val name: String,
    val imageUrl: String,
)
