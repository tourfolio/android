@file:Suppress("ktlint:standard:function-signature")

package com.hdb.tourfolio.feature.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.network.ExploreRepository
import com.hdb.tourfolio.feature.explore.model.ExploreAutocompleteUiState
import com.hdb.tourfolio.feature.explore.model.ExploreCardsUiState
import com.hdb.tourfolio.feature.explore.model.ExploreFilterPreviewUiState
import com.hdb.tourfolio.feature.explore.model.ExploreHubTrendingSpotUiModel
import com.hdb.tourfolio.feature.explore.model.ExploreHubUiState
import com.hdb.tourfolio.feature.explore.model.ExploreMainCardsUiState
import com.hdb.tourfolio.feature.explore.model.ExploreSearchUiState
import com.hdb.tourfolio.feature.explore.model.ExploreSpotDetailUiState
import com.hdb.tourfolio.feature.explore.model.ExploreTrendingUiState
import com.hdb.tourfolio.feature.explore.model.RegionType
import com.hdb.tourfolio.feature.explore.model.TagType
import com.hdb.tourfolio.feature.explore.model.ThemeType
import com.hdb.tourfolio.feature.explore.model.normalizeExploreFilters
import com.hdb.tourfolio.feature.explore.model.normalizeServerTags
import com.hdb.tourfolio.feature.explore.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private val CAROUSEL_TOUR_SPOT_IDS =
    listOf(
        1L,
        2L,
        10L,
    )

private const val FEATURED_CARD_COUNT = 4
private const val RECOMMENDED_CARD_COUNT = 3

private const val AUTOCOMPLETE_LIMIT = 10
private const val AUTOCOMPLETE_DEBOUNCE_MS = 300L

private const val FILTER_PREVIEW_DEBOUNCE_MS = 250L

@HiltViewModel
class ExploreViewModel
    @Inject
    constructor(
        private val exploreRepository: ExploreRepository,
    ) : ViewModel() {
    /*
     * 첫 진입 Carousel
     */
        private val mainCardsState =
            MutableStateFlow<ExploreMainCardsUiState>(
                ExploreMainCardsUiState.Loading,
            )

        val mainCardsUiState: StateFlow<ExploreMainCardsUiState> =
            mainCardsState.asStateFlow()

    /*
     * ExploreScreen 전체 관광지 카드
     */
        private val exploreCardsState =
            MutableStateFlow<ExploreCardsUiState>(
                ExploreCardsUiState.Loading,
            )

        val exploreCardsUiState: StateFlow<ExploreCardsUiState> =
            exploreCardsState.asStateFlow()

    /*
     * 지금 뜨는 여행지
     */
        private val trendingState =
            MutableStateFlow<ExploreTrendingUiState>(
                ExploreTrendingUiState.Loading,
            )

        val trendingUiState: StateFlow<ExploreTrendingUiState> =
            trendingState.asStateFlow()

    /*
     * 검색 화면 콘텐츠 허브
     */
        private val hubState =
            MutableStateFlow<ExploreHubUiState>(
                ExploreHubUiState.Idle,
            )

        val hubUiState: StateFlow<ExploreHubUiState> =
            hubState.asStateFlow()

    /*
     * 검색 결과
     */
        private val searchState =
            MutableStateFlow<ExploreSearchUiState>(
                ExploreSearchUiState.Idle,
            )

        val searchUiState: StateFlow<ExploreSearchUiState> =
            searchState.asStateFlow()

    /*
     * 자동완성
     */
        private val autocompleteState =
            MutableStateFlow<ExploreAutocompleteUiState>(
                ExploreAutocompleteUiState.Idle,
            )

        val autocompleteUiState: StateFlow<ExploreAutocompleteUiState> =
            autocompleteState.asStateFlow()

        private var autocompleteJob: Job? = null

    /*
     * 필터 결과 count 미리보기
     */
        private val filterPreviewState =
            MutableStateFlow<ExploreFilterPreviewUiState>(
                ExploreFilterPreviewUiState.Idle,
            )

        val filterPreviewUiState: StateFlow<ExploreFilterPreviewUiState> =
            filterPreviewState.asStateFlow()

        private var filterPreviewJob: Job? = null

    /*
     * 관광지 상세
     */
        private val spotDetailState =
            MutableStateFlow<ExploreSpotDetailUiState>(
                ExploreSpotDetailUiState.Idle,
            )

        val spotDetailUiState: StateFlow<ExploreSpotDetailUiState> =
            spotDetailState.asStateFlow()

        init {
            fetchMainCards()
            fetchExploreCards()
            fetchTrendingCards()
        }

    /*
     * 첫 진입 풀스크린 Carousel
     */
        fun fetchMainCards() {
            viewModelScope.launch {
                mainCardsState.value =
                    ExploreMainCardsUiState.Loading

                mainCardsState.value =
                    try {
                        val response =
                            exploreRepository.getMainCards()

                        val selectedCards =
                            CAROUSEL_TOUR_SPOT_IDS.mapNotNull { spotId ->
                                response
                                    .firstOrNull { card ->
                                        card.spotId == spotId
                                    }
                                    ?.toUiModel()
                            }

                        ExploreMainCardsUiState.Success(
                            cards = selectedCards,
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        ExploreMainCardsUiState.Error(
                            message =
                                e.message
                                    ?: "탐색 카드를 불러오지 못했습니다.",
                        )
                    }
            }
        }

    /*
     * 전체 관광지 카드
     */
        fun fetchExploreCards() {
            viewModelScope.launch {
                exploreCardsState.value =
                    ExploreCardsUiState.Loading

                exploreCardsState.value =
                    try {
                        val response =
                            exploreRepository
                                .getCards()
                                .map { card ->
                                    card.toUiModel()
                                }

                        val shuffledCards =
                            response.shuffled()

                        val featuredCards =
                            shuffledCards.take(
                                FEATURED_CARD_COUNT,
                            )

                        val remainingCards =
                            shuffledCards.drop(
                                FEATURED_CARD_COUNT,
                            )

                        val recommendedCards =
                            if (
                                remainingCards.size >=
                                RECOMMENDED_CARD_COUNT
                            ) {
                                remainingCards.take(
                                    RECOMMENDED_CARD_COUNT,
                                )
                            } else {
                                response
                                    .shuffled()
                                    .take(
                                        RECOMMENDED_CARD_COUNT,
                                    )
                            }

                        ExploreCardsUiState.Success(
                            featuredCards = featuredCards,
                            recommendedCards = recommendedCards,
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        ExploreCardsUiState.Error(
                            message =
                                e.message
                                    ?: "관광지 목록을 불러오지 못했습니다.",
                        )
                    }
            }
        }

    /*
     * 지금 뜨는 여행지
     */
        fun fetchTrendingCards() {
            viewModelScope.launch {
                trendingState.value =
                    ExploreTrendingUiState.Loading

                trendingState.value =
                    try {
                        val response =
                            exploreRepository
                                .getTrendingCards()
                                .map { card ->
                                    card.toUiModel()
                                }

                        ExploreTrendingUiState.Success(
                            cards = response,
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        ExploreTrendingUiState.Error(
                            message =
                                e.message
                                    ?: "인기 여행지를 불러오지 못했습니다.",
                        )
                    }
            }
        }

    /*
     * 콘텐츠 허브
     */
        fun fetchHub() {
            viewModelScope.launch {
                hubState.value =
                    ExploreHubUiState.Loading

                hubState.value =
                    try {
                        val response =
                            exploreRepository.getHub()

                        ExploreHubUiState.Success(
                            trendingSpots =
                                response.trendingSpots.map { spot ->
                                    ExploreHubTrendingSpotUiModel(
                                        id = spot.spotId,
                                        title = spot.name,
                                        location = spot.location,
                                        popularityRank = spot.popularityRank,
                                        imageUrl = spot.imageUrl,
                                        address = spot.address,
                                    )
                                },
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        ExploreHubUiState.Error(
                            message =
                                e.message
                                    ?: "추천 관광지를 불러오지 못했습니다.",
                        )
                    }
            }
        }

    /*
     * 복합 검색
     */
        fun searchSpots(
            keyword: String? = null,
            tags: Set<TagType> = emptySet(),
            themes: Set<ThemeType> = emptySet(),
            regions: Set<RegionType> = emptySet(),
        ) {
            viewModelScope.launch {
                searchState.value =
                    ExploreSearchUiState.Loading

                searchState.value =
                    try {
                        val filters =
                            normalizeExploreFilters(
                                tags = tags,
                                themes = themes,
                                regions = regions,
                            )

                        val response =
                            exploreRepository.searchSpots(
                                keyword =
                                    keyword
                                        ?.trim()
                                        ?.removePrefix("#")
                                        ?.takeIf { value ->
                                            value.isNotBlank()
                                        },
                                regions =
                                    filters.regions
                                        .map { type ->
                                            type.displayName
                                        }
                                        .takeIf { values ->
                                            values.isNotEmpty()
                                        },
                                themes =
                                    filters.themes
                                        .map { type ->
                                            type.displayName
                                        }
                                        .takeIf { values ->
                                            values.isNotEmpty()
                                        },
                                tags =
                                    filters.tags
                                        .map { type ->
                                            type.displayName
                                        }
                                        .takeIf { values ->
                                            values.isNotEmpty()
                                        },
                            )

                        ExploreSearchUiState.Success(
                            spots =
                                response.spots.map { spot ->
                                    spot.toUiModel()
                                },
                            totalCount = response.totalCount,
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        ExploreSearchUiState.Error(
                            message =
                                e.message
                                    ?: "검색 결과를 불러오지 못했습니다.",
                        )
                    }
            }
        }

    /*
     * 자동완성
     */
        fun requestAutocomplete(
            query: String,
        ) {
            autocompleteJob?.cancel()

            val normalizedQuery =
                query
                    .trim()
                    .removePrefix("#")

            if (normalizedQuery.isBlank()) {
                autocompleteState.value =
                    ExploreAutocompleteUiState.Idle

                return
            }

            autocompleteJob =
                viewModelScope.launch {
                    delay(
                        AUTOCOMPLETE_DEBOUNCE_MS,
                    )

                    autocompleteState.value =
                        ExploreAutocompleteUiState.Loading

                    try {
                        val response =
                            exploreRepository.searchSpots(
                                keyword = normalizedQuery,
                            )

                        val keywords =
                            response.spots
                                .flatMap { spot ->
                                    buildList {
                                        add(
                                            spot.name,
                                        )

                                        add(
                                            spot.location,
                                        )

                                        addAll(
                                            normalizeServerTags(
                                                spot.tags,
                                            ),
                                        )
                                    }
                                }
                                .distinct()
                                .filter { candidate ->
                                    candidate.contains(
                                        other = normalizedQuery,
                                        ignoreCase = true,
                                    )
                                }
                                .sortedWith(
                                    compareBy<String> { candidate ->
                                        if (
                                            candidate.startsWith(
                                                prefix = normalizedQuery,
                                                ignoreCase = true,
                                            )
                                        ) {
                                            0
                                        } else {
                                            1
                                        }
                                    }.thenBy { candidate ->
                                        candidate.length
                                    }.thenBy { candidate ->
                                        candidate
                                    },
                                )
                                .take(
                                    AUTOCOMPLETE_LIMIT,
                                )

                        autocompleteState.value =
                            ExploreAutocompleteUiState.Success(
                                keywords = keywords,
                            )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        autocompleteState.value =
                            ExploreAutocompleteUiState.Error
                    }
                }
        }

    /*
     * 필터 결과 개수 미리보기
     */
        fun previewFilterCount(
            tags: Set<TagType>,
            themes: Set<ThemeType>,
            regions: Set<RegionType>,
        ) {
            filterPreviewJob?.cancel()

            if (
                tags.isEmpty() &&
                themes.isEmpty() &&
                regions.isEmpty()
            ) {
                filterPreviewState.value =
                    ExploreFilterPreviewUiState.Idle

                return
            }

            filterPreviewJob =
                viewModelScope.launch {
                    delay(
                        FILTER_PREVIEW_DEBOUNCE_MS,
                    )

                    filterPreviewState.value =
                        ExploreFilterPreviewUiState.Loading

                    try {
                        val filters =
                            normalizeExploreFilters(
                                tags = tags,
                                themes = themes,
                                regions = regions,
                            )

                        val response =
                            exploreRepository.searchSpots(
                                keyword = null,
                                regions =
                                    filters.regions
                                        .map { type ->
                                            type.displayName
                                        }
                                        .takeIf { values ->
                                            values.isNotEmpty()
                                        },
                                themes =
                                    filters.themes
                                        .map { type ->
                                            type.displayName
                                        }
                                        .takeIf { values ->
                                            values.isNotEmpty()
                                        },
                                tags =
                                    filters.tags
                                        .map { type ->
                                            type.displayName
                                        }
                                        .takeIf { values ->
                                            values.isNotEmpty()
                                        },
                            )

                        filterPreviewState.value =
                            ExploreFilterPreviewUiState.Success(
                                totalCount =
                                    response.totalCount,
                            )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        filterPreviewState.value =
                            ExploreFilterPreviewUiState.Error
                    }
                }
        }

    /*
     * 관광지 상세 조회
     */
        fun fetchSpotDetail(
            spotId: Long,
        ) {
            viewModelScope.launch {
                spotDetailState.value =
                    ExploreSpotDetailUiState.Loading

                spotDetailState.value =
                    try {
                        val response =
                            exploreRepository.getSpotDetail(
                                spotId = spotId,
                            )

                        ExploreSpotDetailUiState.Success(
                            detail =
                                response.toUiModel(),
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        ExploreSpotDetailUiState.Error(
                            message =
                                e.message
                                    ?: "관광지 정보를 불러오지 못했습니다.",
                        )
                    }
            }
        }

        fun clearSpotDetail() {
            spotDetailState.value =
                ExploreSpotDetailUiState.Idle
        }

        fun clearFilterPreview() {
            filterPreviewJob?.cancel()

            filterPreviewState.value =
                ExploreFilterPreviewUiState.Idle
        }

        fun clearAutocomplete() {
            autocompleteJob?.cancel()

            autocompleteState.value =
                ExploreAutocompleteUiState.Idle
        }

        fun clearSearch() {
            searchState.value =
                ExploreSearchUiState.Idle
        }
    }
