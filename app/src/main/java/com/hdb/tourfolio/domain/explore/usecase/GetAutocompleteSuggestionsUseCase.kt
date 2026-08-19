package com.hdb.tourfolio.domain.explore.usecase

import com.hdb.tourfolio.domain.explore.repository.ExploreRepository
import javax.inject.Inject

private const val AUTOCOMPLETE_LIMIT = 10

class GetAutocompleteSuggestionsUseCase
    @Inject
    constructor(
        private val exploreRepository: ExploreRepository,
    ) {
        suspend operator fun invoke(query: String): List<String> {
            val normalizedQuery = query.trim().removePrefix("#")
            if (normalizedQuery.isBlank()) return emptyList()

            val result = exploreRepository.searchSpots(keyword = normalizedQuery)

            return result.spots
                .flatMap { spot -> listOf(spot.name) + spot.tags }
                .distinct()
                .filter { candidate -> candidate.contains(normalizedQuery, ignoreCase = true) }
                .sortedWith(
                    compareBy<String> { candidate ->
                        if (candidate.startsWith(normalizedQuery, ignoreCase = true)) 0 else 1
                    }.thenBy { candidate -> candidate.length }
                        .thenBy { candidate -> candidate },
                ).take(AUTOCOMPLETE_LIMIT)
        }
    }
