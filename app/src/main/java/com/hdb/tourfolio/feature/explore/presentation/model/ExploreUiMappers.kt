package com.hdb.tourfolio.feature.explore.presentation.model

import androidx.annotation.DrawableRes
import com.hdb.tourfolio.R
import com.hdb.tourfolio.domain.explore.model.CityTravelDetail
import com.hdb.tourfolio.domain.explore.model.CityTravelSpot
import com.hdb.tourfolio.domain.explore.model.ExploreAttractionPoint
import com.hdb.tourfolio.domain.explore.model.ExploreCard
import com.hdb.tourfolio.domain.explore.model.ExploreHubTrendingSpot
import com.hdb.tourfolio.domain.explore.model.ExploreMainCard
import com.hdb.tourfolio.domain.explore.model.ExploreSearchSpot
import com.hdb.tourfolio.domain.explore.model.ExploreSpotDetail

fun ExploreMainCard.toUiModel(): ExploreMainCardUiModel =
    ExploreMainCardUiModel(
        id = spotId,
        title = name,
        subTitle = subTitle,
        description = description,
        location = location,
        address = address,
        imageUrl = imageUrl,
        themeType = themeType,
        tags = tags,
    )

fun ExploreCard.toUiModel(): ExploreCardUiModel =
    ExploreCardUiModel(
        id = id,
        title = name,
        areaCode = areaCode,
        areaName = areaName,
        themeTag = themeTag,
        tier = tier,
        imageUrl = imageUrl,
        description = description,
        mapX = mapX,
        mapY = mapY,
        address = address,
        tags = tags,
    )

fun ExploreHubTrendingSpot.toUiModel(): ExploreHubTrendingSpotUiModel =
    ExploreHubTrendingSpotUiModel(
        id = spotId,
        title = name,
        location = location,
        popularityRank = popularityRank,
        imageUrl = imageUrl,
        address = address,
    )

fun ExploreSearchSpot.toUiModel(): ExploreSearchSpotUiModel =
    ExploreSearchSpotUiModel(
        id = spotId,
        title = name,
        location = location,
        address = address,
        imageUrl = imageUrl,
        tags = tags,
    )

fun ExploreSpotDetail.toUiModel(): ExploreSpotDetailUiModel =
    ExploreSpotDetailUiModel(
        id = spotId,
        title = name,
        address = address,
        tags = tags,
        description = description,
        operatingHours = operatingHours.orEmpty().ifBlank { "운영 시간 정보가 없습니다." },
        closedDays = closedDays.orEmpty().ifBlank { "휴무일 정보가 없습니다." },
        admissionFee = admissionFee.orEmpty().ifBlank { "입장료 정보가 없습니다." },
        website = website.orEmpty().ifBlank { "홈페이지 정보가 없습니다." },
        phoneNumber = phoneNumber.orEmpty().ifBlank { "전화번호 정보가 없습니다." },
        attractionPoints = attractionPoints.map { it.toUiModel() },
    )

private fun ExploreAttractionPoint.toUiModel(): ExploreAttractionPointUiModel =
    ExploreAttractionPointUiModel(
        title = title,
        iconType = iconType,
        iconUrl = iconUrl,
    )

fun CityTravelDetail.toUiModel(): CityTravelDetailUiModel =
    CityTravelDetailUiModel(
        id = id,
        categoryTitle = categoryTitle,
        title = title,
        placeCount = placeCount,
        cityImageRes = cityImageKey.toDrawableRes(),
        spots = spots.map { it.toUiModel() },
    )

private fun CityTravelSpot.toUiModel(): CityTravelSpotUiModel =
    CityTravelSpotUiModel(
        id = id,
        title = title,
        imageRes = imageKey.toDrawableRes(),
    )

/*
 * 도시별 추천여행이 정적 데이터소스 기반이라 이미지 리소스 키가 제한적이다.
 * 실제 API 연동 시 imageUrl 기반 AsyncImage로 교체될 자리표시자 매핑.
 */
@DrawableRes
private fun String.toDrawableRes(): Int =
    when (this) {
        "bg_huinnyeoul_demo" -> R.drawable.bg_huinnyeoul_demo
        else -> R.drawable.bg_busan_demo
    }
