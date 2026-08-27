package com.hdb.tourfolio.domain.card.repository

import com.hdb.tourfolio.domain.card.model.CardAcquisition
import com.hdb.tourfolio.domain.card.model.CardCollection
import com.hdb.tourfolio.domain.card.model.CardDetail
import com.hdb.tourfolio.domain.card.model.CardLocation

interface CardRepository {
    suspend fun getCollection(
        region: String? = null,
        theme: String? = null,
        rarity: String? = null,
    ): CardCollection

    suspend fun getCardDetail(cardId: Long): CardDetail

    suspend fun getCardLocation(cardId: Long): CardLocation

    suspend fun acquireCard(cardId: Long): CardAcquisition
}
