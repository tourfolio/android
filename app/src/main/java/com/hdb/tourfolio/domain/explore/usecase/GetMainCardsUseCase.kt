package com.hdb.tourfolio.domain.explore.usecase

import com.hdb.tourfolio.domain.explore.model.ExploreMainCard
import com.hdb.tourfolio.domain.explore.repository.ExploreRepository
import javax.inject.Inject

class GetMainCardsUseCase
    @Inject
    constructor(
        private val exploreRepository: ExploreRepository,
    ) {
        suspend operator fun invoke(): List<ExploreMainCard> = exploreRepository.getMainCards()
    }
