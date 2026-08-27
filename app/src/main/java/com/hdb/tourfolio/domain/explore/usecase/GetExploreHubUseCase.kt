package com.hdb.tourfolio.domain.explore.usecase

import com.hdb.tourfolio.domain.explore.model.ExploreHub
import com.hdb.tourfolio.domain.explore.repository.ExploreRepository
import javax.inject.Inject

class GetExploreHubUseCase
    @Inject
    constructor(
        private val exploreRepository: ExploreRepository,
    ) {
        suspend operator fun invoke(): ExploreHub = exploreRepository.getHub()
    }
