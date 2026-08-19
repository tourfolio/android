package com.hdb.tourfolio.domain.explore.usecase

import com.hdb.tourfolio.domain.explore.model.CityTravelDetail
import com.hdb.tourfolio.domain.explore.repository.ExploreRepository
import javax.inject.Inject

class GetCityTravelDetailUseCase
    @Inject
    constructor(
        private val exploreRepository: ExploreRepository,
    ) {
        suspend operator fun invoke(travelId: Long): CityTravelDetail? = exploreRepository.getCityTravelDetail(travelId)
    }
