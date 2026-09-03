package com.hdb.tourfolio.domain.explore.usecase

import com.hdb.tourfolio.domain.explore.model.ExploreCollection
import com.hdb.tourfolio.domain.explore.repository.ExploreRepository
import javax.inject.Inject

class GetExploreCollectionsUseCase
@Inject
constructor(
    private val exploreRepository: ExploreRepository,
) {
    suspend operator fun invoke(): List<ExploreCollection> =
        exploreRepository.getCollections()
}