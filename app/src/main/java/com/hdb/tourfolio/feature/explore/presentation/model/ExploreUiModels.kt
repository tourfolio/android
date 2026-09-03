package com.hdb.tourfolio.feature.explore.presentation.model

import com.hdb.tourfolio.domain.common.model.ThemeType

/*
 * 투어 컬렉션 목록
 */
data class ExploreCollectionUiModel(
    val id: Long,
    val title: String,
    val thumbnailUrl: String,
    val placeCount: Int,
)

/*
 * 투어 컬렉션 상세
 */
data class ExploreCollectionDetailUiModel(
    val id: Long,
    val title: String,
    val placeCount: Int,
    val spots: List<ExploreCollectionSpotUiModel>,
)

data class ExploreCollectionSpotUiModel(
    val id: Long,
    val title: String,
    val imageUrl: String,
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
    val imageUrl: String,
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
