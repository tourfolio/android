package com.hdb.tourfolio.domain.card.usecase

import com.hdb.tourfolio.domain.card.model.CardDetail
import com.hdb.tourfolio.domain.card.repository.CardRepository
import javax.inject.Inject

class GetCardDetailUseCase
    @Inject
    constructor(
        private val cardRepository: CardRepository,
    ) {
        suspend operator fun invoke(cardId: Long): CardDetail = cardRepository.getCardDetail(cardId)
    }
