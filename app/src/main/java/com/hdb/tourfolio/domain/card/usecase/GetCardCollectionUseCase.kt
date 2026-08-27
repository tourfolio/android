package com.hdb.tourfolio.domain.card.usecase

import com.hdb.tourfolio.domain.card.model.CardCollection
import com.hdb.tourfolio.domain.card.repository.CardRepository
import javax.inject.Inject

class GetCardCollectionUseCase
    @Inject
    constructor(
        private val cardRepository: CardRepository,
    ) {
        suspend operator fun invoke(
            region: String? = null,
            theme: String? = null,
            rarity: String? = null,
        ): CardCollection = cardRepository.getCollection(region, theme, rarity)
    }
