package com.hdb.tourfolio.domain.explore.repository

import com.hdb.tourfolio.domain.explore.model.ExploreCard
import com.hdb.tourfolio.domain.explore.model.ExploreCollection
import com.hdb.tourfolio.domain.explore.model.ExploreCollectionDetail
import com.hdb.tourfolio.domain.explore.model.ExploreHub
import com.hdb.tourfolio.domain.explore.model.ExploreMainCard
import com.hdb.tourfolio.domain.explore.model.ExploreSearchResult
import com.hdb.tourfolio.domain.explore.model.ExploreSpotDetail

interface ExploreRepository {
    suspend fun getMainCards(): List<ExploreMainCard>

    suspend fun getCards(): List<ExploreCard>

    suspend fun getTrendingCards(): List<ExploreCard>

    suspend fun getHub(): ExploreHub

    suspend fun searchSpots(
        keyword: String? = null,
        regions: List<String>? = null,
        themes: List<String>? = null,
        tags: List<String>? = null,
    ): ExploreSearchResult

    suspend fun getSpotDetail(spotId: Long): ExploreSpotDetail

    suspend fun getCollections(): List<ExploreCollection>

    suspend fun getCollectionDetail(
        collectionId: Long,
    ): ExploreCollectionDetail
}
