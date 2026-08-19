package com.hdb.tourfolio.domain.explore.usecase

import com.hdb.tourfolio.domain.common.model.RegionType
import com.hdb.tourfolio.domain.common.model.TagType
import com.hdb.tourfolio.domain.common.model.ThemeType
import com.hdb.tourfolio.domain.explore.model.ExploreSearchResult
import com.hdb.tourfolio.domain.explore.repository.ExploreRepository
import javax.inject.Inject

class SearchExploreSpotsUseCase
    @Inject
    constructor(
        private val exploreRepository: ExploreRepository,
    ) {
        suspend operator fun invoke(
            keyword: String? = null,
            tags: Set<TagType> = emptySet(),
            themes: Set<ThemeType> = emptySet(),
            regions: Set<RegionType> = emptySet(),
        ): ExploreSearchResult {
            val normalized = normalizeExploreFilters(tags, themes, regions)
            return exploreRepository.searchSpots(
                keyword = keyword,
                regions = normalized.regions.map { it.displayName }.takeIf { it.isNotEmpty() },
                themes = normalized.themes.map { it.displayName }.takeIf { it.isNotEmpty() },
                tags = normalized.tags.map { it.displayName }.takeIf { it.isNotEmpty() },
            )
        }
    }
