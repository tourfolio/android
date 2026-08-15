package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.CardAcquireDto
import com.hdb.tourfolio.core.network.dto.CardCollectionDto
import com.hdb.tourfolio.core.network.dto.CardDetailDto
import com.hdb.tourfolio.core.network.dto.CardLocationDto
import javax.inject.Inject

class CardRepository
@Inject
constructor(
    private val cardApiService: CardApiService,
) {
    /*
     * 수집 메인 조회
     */
    suspend fun getCollection(
        region: String? = null,
        theme: String? = null,
        rarity: String? = null,
    ): CardCollectionDto =
        cardApiService.getCollection(
            region = region,
            theme = theme,
            rarity = rarity,
        )

    /*
     * 카드 상세 조회
     */
    suspend fun getCardDetail(
        cardId: Long,
    ): CardDetailDto =
        cardApiService.getCardDetail(
            cardId = cardId,
        )

    /*
     * 카드 관광지 좌표 조회
     */
    suspend fun getCardLocation(
        cardId: Long,
    ): CardLocationDto =
        cardApiService.getCardLocation(
            cardId = cardId,
        )

    /*
     * 카드 획득
     */
    suspend fun acquireCard(
        cardId: Long,
    ): CardAcquireDto =
        cardApiService.acquireCard(
            cardId = cardId,
        )
}