package com.hdb.tourfolio.feature.explore.model

import androidx.annotation.DrawableRes

/*
 * 지역별 추천 여행지 상세
 */
data class CityTravelDetailUiModel(
    val id: Long,
    val categoryTitle: String,
    val title: String,
    val placeCount: Int,
    @DrawableRes val cityImageRes: Int,
    val spots: List<CityTravelSpotUiModel>,
)

/*
 * 각 지역별 추천여행 코스
 */
data class CityTravelSpotUiModel(
    val id: Long,
    val title: String,
    @DrawableRes val imageRes: Int,
)

/*
 * 탐색 첫 진입 풀스크린 Carousel
 */
data class ExploreMainCardUiModel(
    val id: Long,
    val title: String,
    val subTitle: String,
    val description: String,
    val location: String,
    val address: String,
    val imageUrl: String,
    val themeType: ThemeType,
    val tags: List<String>,
)

/*
 * ExploreScreen 관광지 카드
 */
data class ExploreCardUiModel(
    val id: Long,
    val title: String,
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

/*
 * 검색 화면 콘텐츠 허브
 */
data class ExploreHubTrendingSpotUiModel(
    val id: Long,
    val title: String,
    val location: String,
    val popularityRank: Int,
    val imageUrl: String,
    val address: String,
)

/*
 * 복합 검색 결과
 */
data class ExploreSearchSpotUiModel(
    val id: Long,
    val title: String,
    val location: String,
    val address: String,
    val imageUrl: String,
    val tags: List<String>,
)

/*
 * 관광지 상세
 */
data class ExploreSpotDetailUiModel(
    val id: Long,
    val title: String,
    val address: String,
    val tags: List<String>,
    val description: String,
    val operatingHours: String,
    val closedDays: String,
    val admissionFee: String,
    val website: String,
    val phoneNumber: String,
    val attractionPoints: List<ExploreAttractionPointUiModel>,
)

data class ExploreAttractionPointUiModel(
    val title: String,
    val iconType: String,
    val iconUrl: String?,
)

/*
 * 검색/필터 API 호출 전에 정규화된 필터 상태
 */
internal data class NormalizedExploreFilters(
    val tags: Set<TagType>,
    val themes: Set<ThemeType>,
    val regions: Set<RegionType>,
)
