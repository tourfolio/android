package com.hdb.tourfolio.feature.explore.presentation.model

import com.hdb.tourfolio.domain.explore.model.ExploreAttractionPoint
import com.hdb.tourfolio.domain.explore.model.ExploreCard
import com.hdb.tourfolio.domain.explore.model.ExploreCollection
import com.hdb.tourfolio.domain.explore.model.ExploreCollectionDetail
import com.hdb.tourfolio.domain.explore.model.ExploreCollectionSpot
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
        hasImage = hasImage,
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
        hasImage = hasImage,
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
        hasImage = hasImage,
        address = address,
    )

fun ExploreSearchSpot.toUiModel(): ExploreSearchSpotUiModel =
    ExploreSearchSpotUiModel(
        id = spotId,
        title = name,
        location = location,
        address = address,
        imageUrl = imageUrl,
        hasImage = hasImage,
        tags = tags,
    )

fun ExploreSpotDetail.toUiModel(): ExploreSpotDetailUiModel =
    ExploreSpotDetailUiModel(
        id = spotId,
        title = name,
        imageUrl = imageUrl,
        hasImage = hasImage,
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

fun ExploreCollection.toUiModel(): ExploreCollectionUiModel =
    ExploreCollectionUiModel(
        id = collectionId,
        title = title,
        thumbnailUrl = thumbnailUrl,
        placeCount = placeCount,
    )

fun ExploreCollectionDetail.toUiModel(): ExploreCollectionDetailUiModel =
    ExploreCollectionDetailUiModel(
        id = collectionId,
        title = title,
        placeCount = placeCount,
        spots =
            spots.map { spot ->
                spot.toUiModel()
            },
    )

private fun ExploreCollectionSpot.toUiModel(): ExploreCollectionSpotUiModel =
    ExploreCollectionSpotUiModel(
        id = spotId,
        title = name,
        imageUrl = imageUrl.orEmpty(),
        hasImage = hasImage,
    )
