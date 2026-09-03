package com.hdb.tourfolio.domain.explore.usecase

import com.hdb.tourfolio.domain.explore.model.ExploreCollectionDetail
import com.hdb.tourfolio.domain.explore.repository.ExploreRepository
import javax.inject.Inject

class GetExploreCollectionDetailUseCase
    @Inject
    constructor(
        private val exploreRepository: ExploreRepository,
    ) {
        suspend operator fun invoke(collectionId: Long): ExploreCollectionDetail =
            exploreRepository.getCollectionDetail(
                collectionId = collectionId,
            )
    }
