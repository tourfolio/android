package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.ExploreCardDto
import com.hdb.tourfolio.core.network.dto.ExploreHubDto
import com.hdb.tourfolio.core.network.dto.ExploreMainCardDto
import com.hdb.tourfolio.core.network.dto.ExploreSearchDto
import com.hdb.tourfolio.core.network.dto.ExploreSpotDetailDto
import javax.inject.Inject

class ExploreRepository
    @Inject
    constructor(
        private val exploreApiService: ExploreApiService,
    ) {
        suspend fun getMainCards(): List<ExploreMainCardDto> = exploreApiService.getMainCards()

        suspend fun getCards(): List<ExploreCardDto> = exploreApiService.getCards()

        suspend fun getTrendingCards(): List<ExploreCardDto> = exploreApiService.getTrendingCards()

        suspend fun getHub(): ExploreHubDto = exploreApiService.getHub()

        suspend fun searchSpots(
            keyword: String? = null,
            regions: List<String>? = null,
            themes: List<String>? = null,
            tags: List<String>? = null,
        ): ExploreSearchDto =
            exploreApiService.searchSpots(
                keyword = keyword,
                regions = regions,
                themes = themes,
                tags = tags,
            )

        suspend fun getSpotDetail(spotId: Long): ExploreSpotDetailDto =
            exploreApiService.getSpotDetail(
                spotId = spotId,
            )
    }
