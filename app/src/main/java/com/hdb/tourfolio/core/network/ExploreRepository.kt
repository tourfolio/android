package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.ExploreCardDto
import com.hdb.tourfolio.core.network.dto.ExploreMainCardDto
import javax.inject.Inject

class ExploreRepository
@Inject
constructor(
    private val exploreApiService: ExploreApiService,
) {
    suspend fun getMainCards(): List<ExploreMainCardDto> =
        exploreApiService.getMainCards()

    suspend fun getCards(): List<ExploreCardDto> =
        exploreApiService.getCards()

    suspend fun getTrendingCards(): List<ExploreCardDto> =
        exploreApiService.getTrendingCards()
}