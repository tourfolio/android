package com.hdb.tourfolio.data.card

import com.hdb.tourfolio.data.card.mapper.toDomain
import com.hdb.tourfolio.data.card.remote.CardApiService
import com.hdb.tourfolio.domain.card.model.CardAcquisition
import com.hdb.tourfolio.domain.card.model.CardCollection
import com.hdb.tourfolio.domain.card.model.CardDetail
import com.hdb.tourfolio.domain.card.model.CardLocation
import com.hdb.tourfolio.domain.card.repository.CardRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryImpl
    @Inject
    constructor(
        private val cardApiService: CardApiService,
    ) : CardRepository {
        override suspend fun getCollection(
            region: String?,
            theme: String?,
            rarity: String?,
        ): CardCollection =
            cardApiService
                .getCollection(region = region, theme = theme, rarity = rarity)
                .toDomain()

        override suspend fun getCardDetail(cardId: Long): CardDetail = cardApiService.getCardDetail(cardId).toDomain()

        override suspend fun getCardLocation(cardId: Long): CardLocation = cardApiService.getCardLocation(cardId).toDomain()

        override suspend fun acquireCard(cardId: Long): CardAcquisition = cardApiService.acquireCard(cardId).toDomain()
    }
