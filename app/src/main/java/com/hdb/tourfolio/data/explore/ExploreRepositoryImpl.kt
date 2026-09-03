package com.hdb.tourfolio.data.explore

import com.hdb.tourfolio.data.explore.mapper.toDomain
import com.hdb.tourfolio.data.explore.mapper.toDomainOrNull
import com.hdb.tourfolio.data.explore.remote.ExploreApiService
import com.hdb.tourfolio.domain.explore.model.ExploreCard
import com.hdb.tourfolio.domain.explore.model.ExploreCollection
import com.hdb.tourfolio.domain.explore.model.ExploreCollectionDetail
import com.hdb.tourfolio.domain.explore.model.ExploreHub
import com.hdb.tourfolio.domain.explore.model.ExploreMainCard
import com.hdb.tourfolio.domain.explore.model.ExploreSearchResult
import com.hdb.tourfolio.domain.explore.model.ExploreSpotDetail
import com.hdb.tourfolio.domain.explore.repository.ExploreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreRepositoryImpl
    @Inject
    constructor(
        private val exploreApiService: ExploreApiService,
    ) : ExploreRepository {
        override suspend fun getMainCards(): List<ExploreMainCard> =
            exploreApiService
                .getMainCards()
                .mapNotNull { it.toDomainOrNull() }

        override suspend fun getCards(): List<ExploreCard> = exploreApiService.getCards().map { it.toDomain() }

        override suspend fun getTrendingCards(): List<ExploreCard> = exploreApiService.getTrendingCards().map { it.toDomain() }

        override suspend fun getHub(): ExploreHub = exploreApiService.getHub().toDomain()

        override suspend fun searchSpots(
            keyword: String?,
            regions: List<String>?,
            themes: List<String>?,
            tags: List<String>?,
        ): ExploreSearchResult =
            exploreApiService
                .searchSpots(keyword = keyword, regions = regions, themes = themes, tags = tags)
                .toDomain()

        override suspend fun getSpotDetail(spotId: Long): ExploreSpotDetail = exploreApiService.getSpotDetail(spotId).toDomain()

        override suspend fun getCollections(): List<ExploreCollection> =
            exploreApiService
                .getCollections()
                .map { collection ->
                    collection.toDomain()
                }

        override suspend fun getCollectionDetail(
            collectionId: Long,
        ): ExploreCollectionDetail =
            exploreApiService
                .getCollectionDetail(
                    collectionId = collectionId,
                )
                .toDomain()
    }
