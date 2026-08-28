package com.hdb.tourfolio.data.explore.mapper

import com.hdb.tourfolio.data.explore.remote.dto.ExploreAttractionPointDto
import com.hdb.tourfolio.data.explore.remote.dto.ExploreCardDto
import com.hdb.tourfolio.data.explore.remote.dto.ExploreHubDto
import com.hdb.tourfolio.data.explore.remote.dto.ExploreMainCardDto
import com.hdb.tourfolio.data.explore.remote.dto.ExploreSearchDto
import com.hdb.tourfolio.data.explore.remote.dto.ExploreSearchSpotDto
import com.hdb.tourfolio.data.explore.remote.dto.ExploreSpotDetailDto
import com.hdb.tourfolio.domain.common.model.ThemeType
import com.hdb.tourfolio.domain.explore.model.ExploreAttractionPoint
import com.hdb.tourfolio.domain.explore.model.ExploreCard
import com.hdb.tourfolio.domain.explore.model.ExploreHub
import com.hdb.tourfolio.domain.explore.model.ExploreHubTheme
import com.hdb.tourfolio.domain.explore.model.ExploreHubTrendingSpot
import com.hdb.tourfolio.domain.explore.model.ExploreMainCard
import com.hdb.tourfolio.domain.explore.model.ExploreNearbySpot
import com.hdb.tourfolio.domain.explore.model.ExploreSearchResult
import com.hdb.tourfolio.domain.explore.model.ExploreSearchSpot
import com.hdb.tourfolio.domain.explore.model.ExploreSpotDetail

/*
 * 서버가 지원하지 않는(미인식) 테마 문자열을 보내면 null을 반환한다.
 * 카드 하나 때문에 목록 전체 조회가 실패하지 않도록 호출부에서 mapNotNull로 걸러낸다.
 */
fun ExploreMainCardDto.toDomainOrNull(): ExploreMainCard? {
    val themeType = ThemeType.fromDisplayName(theme) ?: return null

    return ExploreMainCard(
        spotId = spotId,
        name = name,
        subTitle = subTitle,
        description = description,
        location = location,
        address = address,
        imageUrl = imageUrl,
        themeType = themeType,
        tags = normalizeServerTags(tags),
        totalCount = totalCount,
        currentIndex = currentIndex,
    )
}

fun ExploreCardDto.toDomain(): ExploreCard =
    ExploreCard(
        id = id,
        name = name,
        areaCode = areaCode,
        areaName = areaName,
        themeTag = themeTag,
        tier = tier,
        imageUrl = imageUrl,
        description = description,
        mapX = mapX,
        mapY = mapY,
        address = address,
        tags = normalizeServerTags(tags),
    )

fun ExploreHubDto.toDomain(): ExploreHub =
    ExploreHub(
        themes =
            themes.map { theme ->
                ExploreHubTheme(
                    themeId = theme.themeId,
                    title = theme.title,
                    placeCount = theme.placeCount,
                    imageUrl = theme.imageUrl,
                )
            },
        trendingSpots =
            trendingSpots.map { spot ->
                ExploreHubTrendingSpot(
                    spotId = spot.spotId,
                    name = spot.name,
                    location = spot.location,
                    popularityRank = spot.popularityRank,
                    imageUrl = spot.imageUrl,
                    address = spot.address,
                )
            },
    )

fun ExploreSearchDto.toDomain(): ExploreSearchResult =
    ExploreSearchResult(
        spots = spots.map { it.toDomain() },
        totalCount = totalCount,
    )

fun ExploreSearchSpotDto.toDomain(): ExploreSearchSpot =
    ExploreSearchSpot(
        spotId = spotId,
        name = name,
        location = location,
        address = address,
        imageUrl = imageUrl,
        tags = normalizeServerTags(tags),
    )

fun ExploreSpotDetailDto.toDomain(): ExploreSpotDetail =
    ExploreSpotDetail(
        spotId = spotId,
        name = name,
        imageUrl = imageUrl,
        address = address,
        tags = normalizeServerTags(tags),
        description = description,
        operatingHours = operatingHours,
        closedDays = closedDays,
        admissionFee = admissionFee,
        website = website,
        phoneNumber = phoneNumber,
        attractionPoints = attractionPoints.map { it.toDomain() },
        nearbySpots =
            nearbySpots.map { spot ->
                ExploreNearbySpot(
                    spotId = spot.spotId,
                    name = spot.name,
                    imageUrl = spot.imageUrl,
                )
            },
    )

private fun ExploreAttractionPointDto.toDomain(): ExploreAttractionPoint =
    ExploreAttractionPoint(
        title = title,
        iconType = iconType,
        iconUrl = iconUrl,
    )

/*
 * 서버에서 전달되는 태그 정규화
 * ["역사, 궁궐, 국보"] -> ["역사", "궁궐", "국보"]
 */
fun normalizeServerTags(tags: List<String>): List<String> =
    tags
        .flatMap { tagGroup -> tagGroup.split(",") }
        .map { tag -> tag.trim() }
        .filter { tag -> tag.isNotBlank() }
        .distinct()
