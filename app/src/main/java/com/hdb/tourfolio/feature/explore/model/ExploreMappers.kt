package com.hdb.tourfolio.feature.explore.model

import com.hdb.tourfolio.core.network.dto.ExploreCardDto
import com.hdb.tourfolio.core.network.dto.ExploreMainCardDto
import com.hdb.tourfolio.core.network.dto.ExploreSearchSpotDto
import com.hdb.tourfolio.core.network.dto.ExploreSpotDetailDto

/*
 * 첫 진입 Carousel DTO → UI Model
 */
internal fun ExploreMainCardDto.toUiModel(): ExploreMainCardUiModel {
    val mappedTheme =
        ThemeType.entries.firstOrNull { type ->
            type.displayName == theme
        } ?: throw IllegalArgumentException(
            "지원하지 않는 관광지 테마입니다: $theme",
        )

    return ExploreMainCardUiModel(
        id = spotId,
        title = name,
        subTitle = subTitle,
        description = description,
        location = location,
        address = address,
        imageUrl = imageUrl,
        themeType = mappedTheme,
        tags = normalizeServerTags(tags),
    )
}

/*
 * 일반 관광지 DTO → UI Model
 */
internal fun ExploreCardDto.toUiModel(): ExploreCardUiModel =
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
        tags = normalizeServerTags(tags),
    )

/*
 * 검색 DTO → UI Model
 */
internal fun ExploreSearchSpotDto.toUiModel(): ExploreSearchSpotUiModel =
    ExploreSearchSpotUiModel(
        id = spotId,
        title = name,
        location = location,
        address = address,
        imageUrl = imageUrl,
        tags = normalizeServerTags(tags),
    )

/*
 * 관광지 상세 DTO → UI Model
 */
internal fun ExploreSpotDetailDto.toUiModel(): ExploreSpotDetailUiModel =
    ExploreSpotDetailUiModel(
        id = spotId,
        title = name,
        address = address,
        tags = normalizeServerTags(tags),
        description = description,
        operatingHours =
            operatingHours
                .orEmpty()
                .ifBlank {
                    "운영 시간 정보가 없습니다."
                },
        closedDays =
            closedDays
                .orEmpty()
                .ifBlank {
                    "휴무일 정보가 없습니다."
                },
        admissionFee =
            admissionFee
                .orEmpty()
                .ifBlank {
                    "입장료 정보가 없습니다."
                },
        website =
            website
                .orEmpty()
                .ifBlank {
                    "홈페이지 정보가 없습니다."
                },
        phoneNumber =
            phoneNumber
                .orEmpty()
                .ifBlank {
                    "전화번호 정보가 없습니다."
                },
        attractionPoints =
            attractionPoints.map { point ->
                ExploreAttractionPointUiModel(
                    title = point.title,
                    iconType = point.iconType,
                    iconUrl = point.iconUrl,
                )
            },
    )

/*
 * 서버에서 전달되는 태그 정규화
 * ["역사, 궁궐, 국보"] -> ["역사", "궁궐", "국보"]
 */
internal fun normalizeServerTags(tags: List<String>): List<String> =
    tags
        .flatMap { tagGroup ->
            tagGroup.split(",")
        }
        .map { tag ->
            tag.trim()
        }
        .filter { tag ->
            tag.isNotBlank()
        }
        .distinct()
