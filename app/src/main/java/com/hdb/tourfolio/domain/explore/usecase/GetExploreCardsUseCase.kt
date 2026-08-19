package com.hdb.tourfolio.domain.explore.usecase

import com.hdb.tourfolio.domain.explore.model.ExploreCard
import com.hdb.tourfolio.domain.explore.repository.ExploreRepository
import javax.inject.Inject

class GetExploreCardsUseCase
    @Inject
    constructor(
        private val exploreRepository: ExploreRepository,
    ) {
        suspend operator fun invoke(): List<ExploreCard> = exploreRepository.getCards()
    }
