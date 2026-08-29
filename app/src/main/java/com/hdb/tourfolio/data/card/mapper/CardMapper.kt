package com.hdb.tourfolio.data.card.mapper

import com.hdb.tourfolio.data.card.remote.dto.CardAcquireDto
import com.hdb.tourfolio.data.card.remote.dto.CardCollectionDto
import com.hdb.tourfolio.data.card.remote.dto.CardDetailDto
import com.hdb.tourfolio.data.card.remote.dto.CardItemDto
import com.hdb.tourfolio.data.card.remote.dto.CardLocationDto
import com.hdb.tourfolio.domain.card.model.Card
import com.hdb.tourfolio.domain.card.model.CardAcquisition
import com.hdb.tourfolio.domain.card.model.CardCollection
import com.hdb.tourfolio.domain.card.model.CardDetail
import com.hdb.tourfolio.domain.card.model.CardLocation
import com.hdb.tourfolio.domain.card.model.CardRarity

fun CardItemDto.toDomain(): Card =
    Card(
        cardId = cardId,
        spotName = normalizeSpotName(spotName),
        imageUrl = imageUrl,
        rarity = CardRarity.valueOf(rarity),
        theme = theme,
        region = region,
        isOwned = isOwned,
    )

fun CardCollectionDto.toDomain(): CardCollection =
    CardCollection(
        collectionRate = summary.collectionRate,
        ownedCount = summary.ownedCount,
        totalCount = summary.totalCount,
        filteredCount = filteredCount,
        cards = cards.map { it.toDomain() },
    )

fun CardDetailDto.toDomain(): CardDetail =
    CardDetail(
        cardId = cardId,
        name = normalizeSpotName(name),
        address = address,
        rarity = CardRarity.valueOf(rarity),
        theme = theme,
        imageUrl = imageUrl,
        glowColorCode = glowColorCode,
        cardNumber = cardNumber,
        phrase = phrase,
        isOwned = isOwned,
        acquiredAt = acquiredAt,
        acquisitionPath = acquisitionPath,
        message = message,
    )

fun CardLocationDto.toDomain(): CardLocation =
    CardLocation(
        cardId = cardId,
        spotName = normalizeSpotName(spotName),
        latitude = latitude,
        longitude = longitude,
    )

fun CardAcquireDto.toDomain(): CardAcquisition =
    CardAcquisition(
        cardId = cardId,
        cardName = normalizeSpotName(cardName),
        rarity = CardRarity.valueOf(rarity),
        acquiredAt = acquiredAt,
    )

/*
 * spotName 정보 버그 처리 (추후 서버측에 수정 요청 예정)
 * "A / A" 처럼 슬래시로 중복된 이름이 오면 하나로 합친다.
 */
fun normalizeSpotName(spotName: String): String {
    val parts =
        spotName
            .split("/")
            .map { it.trim() }
            .filter { it.isNotBlank() }

    return if (parts.size >= 2 && parts.distinct().size == 1) {
        parts.first()
    } else {
        spotName
    }
}
