package com.hdb.tourfolio.feature.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.network.ExploreRepository
import com.hdb.tourfolio.core.network.dto.ExploreCardDto
import com.hdb.tourfolio.core.network.dto.ExploreMainCardDto
import com.hdb.tourfolio.core.network.dto.ExploreSearchSpotDto
import com.hdb.tourfolio.core.network.dto.ExploreSpotDetailDto
import com.hdb.tourfolio.feature.explore.model.RegionType
import com.hdb.tourfolio.feature.explore.model.TagType
import com.hdb.tourfolio.feature.explore.model.ThemeType
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

/*
 * ---------------------------------------------------------
 * 탐색 첫 진입 풀스크린 Carousel
 * ---------------------------------------------------------
 */
data class ExploreMainCardUiModel(
    val id: Long,
    val title: String,
    val subTitle: String,
    val description: String,
    val location: String,
    val address: String,
    val imageUrl: String,
    val themeType: ThemeType,
    val tags: List<String>,
)

sealed interface ExploreMainCardsUiState {
    data object Loading : ExploreMainCardsUiState

    data class Success(
        val cards: List<ExploreMainCardUiModel>,
    ) : ExploreMainCardsUiState

    data class Error(
        val message: String,
    ) : ExploreMainCardsUiState
}

/*
 * ---------------------------------------------------------
 * ExploreScreen 관광지 카드
 * ---------------------------------------------------------
 */
data class ExploreCardUiModel(
    val id: Long,
    val title: String,
    val areaCode: String,
    val areaName: String,
    val themeTag: String,
    val tier: Int,
    val imageUrl: String,
    val description: String,
    val mapX: String,
    val mapY: String,
    val address: String,
    val tags: List<String>,
)

sealed interface ExploreCardsUiState {
    data object Loading : ExploreCardsUiState

    data class Success(
        val featuredCards: List<ExploreCardUiModel>,
        val recommendedCards: List<ExploreCardUiModel>,
    ) : ExploreCardsUiState

    data class Error(
        val message: String,
    ) : ExploreCardsUiState
}

/*
 * ---------------------------------------------------------
 * 지금 뜨는 여행지
 * ---------------------------------------------------------
 */
sealed interface ExploreTrendingUiState {
    data object Loading : ExploreTrendingUiState

    data class Success(
        val cards: List<ExploreCardUiModel>,
    ) : ExploreTrendingUiState

    data class Error(
        val message: String,
    ) : ExploreTrendingUiState
}

/*
 * ---------------------------------------------------------
 * 콘텐츠 허브
 * ---------------------------------------------------------
 */
data class ExploreHubTrendingSpotUiModel(
    val id: Long,
    val title: String,
    val location: String,
    val popularityRank: Int,
    val imageUrl: String,
    val address: String,
)

sealed interface ExploreHubUiState {
    data object Idle : ExploreHubUiState

    data object Loading : ExploreHubUiState

    data class Success(
        val trendingSpots: List<ExploreHubTrendingSpotUiModel>,
    ) : ExploreHubUiState

    data class Error(
        val message: String,
    ) : ExploreHubUiState
}

/*
 * ---------------------------------------------------------
 * 복합 검색 결과
 * ---------------------------------------------------------
 */
data class ExploreSearchSpotUiModel(
    val id: Long,
    val title: String,
    val location: String,
    val address: String,
    val imageUrl: String,
    val tags: List<String>,
)

sealed interface ExploreSearchUiState {
    data object Idle : ExploreSearchUiState

    data object Loading : ExploreSearchUiState

    data class Success(
        val spots: List<ExploreSearchSpotUiModel>,
        val totalCount: Int,
    ) : ExploreSearchUiState

    data class Error(
        val message: String,
    ) : ExploreSearchUiState
}

/*
 * ---------------------------------------------------------
 * 자동완성
 * ---------------------------------------------------------
 */
sealed interface ExploreAutocompleteUiState {
    data object Idle : ExploreAutocompleteUiState

    data object Loading : ExploreAutocompleteUiState

    data class Success(
        val keywords: List<String>,
    ) : ExploreAutocompleteUiState

    data object Error : ExploreAutocompleteUiState
}

/*
 * ---------------------------------------------------------
 * 필터 결과 개수 미리보기
 * ---------------------------------------------------------
 */
sealed interface ExploreFilterPreviewUiState {
    data object Idle : ExploreFilterPreviewUiState

    data object Loading : ExploreFilterPreviewUiState

    data class Success(
        val totalCount: Int,
    ) : ExploreFilterPreviewUiState

    data object Error : ExploreFilterPreviewUiState
}

/*
 * ---------------------------------------------------------
 * 관광지 상세
 *
 * GET /api/v1/explore/spots/{spotId}
 * ---------------------------------------------------------
 */
data class ExploreSpotDetailUiModel(
    val id: Long,
    val title: String,
    val address: String,
    val tags: List<String>,
    val description: String,
    val operatingHours: String,
    val closedDays: String,
    val admissionFee: String,
    val website: String,
    val phoneNumber: String,
    val attractionPoints: List<ExploreAttractionPointUiModel>,
)

data class ExploreAttractionPointUiModel(
    val title: String,
    val iconType: String,
    val iconUrl: String?,
)

sealed interface ExploreSpotDetailUiState {
    data object Idle : ExploreSpotDetailUiState

    data object Loading : ExploreSpotDetailUiState

    data class Success(
        val detail: ExploreSpotDetailUiModel,
    ) : ExploreSpotDetailUiState

    data class Error(
        val message: String,
    ) : ExploreSpotDetailUiState
}

@HiltViewModel
class ExploreViewModel
    @Inject
    constructor(
        private val exploreRepository: ExploreRepository,
    ) : ViewModel() {
    /*
     * ---------------------------------------------------------
     * 첫 진입 Carousel
     * ---------------------------------------------------------
     */
        private val mainCardsState =
            MutableStateFlow<ExploreMainCardsUiState>(
                ExploreMainCardsUiState.Loading,
            )

        val mainCardsUiState: StateFlow<ExploreMainCardsUiState> =
            mainCardsState.asStateFlow()

    /*
     * ---------------------------------------------------------
     * ExploreScreen 전체 관광지 카드
     * ---------------------------------------------------------
     */
        private val exploreCardsState =
            MutableStateFlow<ExploreCardsUiState>(
                ExploreCardsUiState.Loading,
            )

        val exploreCardsUiState: StateFlow<ExploreCardsUiState> =
            exploreCardsState.asStateFlow()

    /*
     * ---------------------------------------------------------
     * 지금 뜨는 여행지
     * ---------------------------------------------------------
     */
        private val trendingState =
            MutableStateFlow<ExploreTrendingUiState>(
                ExploreTrendingUiState.Loading,
            )

        val trendingUiState: StateFlow<ExploreTrendingUiState> =
            trendingState.asStateFlow()

    /*
     * ---------------------------------------------------------
     * 검색 화면 콘텐츠 허브
     * ---------------------------------------------------------
     */
        private val hubState =
            MutableStateFlow<ExploreHubUiState>(
                ExploreHubUiState.Idle,
            )

        val hubUiState: StateFlow<ExploreHubUiState> =
            hubState.asStateFlow()

    /*
     * ---------------------------------------------------------
     * 검색 결과
     * ---------------------------------------------------------
     */
        private val searchState =
            MutableStateFlow<ExploreSearchUiState>(
                ExploreSearchUiState.Idle,
            )

        val searchUiState: StateFlow<ExploreSearchUiState> =
            searchState.asStateFlow()

    /*
     * ---------------------------------------------------------
     * 자동완성
     * ---------------------------------------------------------
     */
        private val autocompleteState =
            MutableStateFlow<ExploreAutocompleteUiState>(
                ExploreAutocompleteUiState.Idle,
            )

        val autocompleteUiState: StateFlow<ExploreAutocompleteUiState> =
            autocompleteState.asStateFlow()

        private var autocompleteJob: Job? = null

    /*
     * ---------------------------------------------------------
     * 필터 결과 count 미리보기
     * ---------------------------------------------------------
     */
        private val filterPreviewState =
            MutableStateFlow<ExploreFilterPreviewUiState>(
                ExploreFilterPreviewUiState.Idle,
            )

        val filterPreviewUiState: StateFlow<ExploreFilterPreviewUiState> =
            filterPreviewState.asStateFlow()

        private var filterPreviewJob: Job? = null

    /*
     * ---------------------------------------------------------
     * 관광지 상세
     * ---------------------------------------------------------
     */
        private val spotDetailState =
            MutableStateFlow<ExploreSpotDetailUiState>(
                ExploreSpotDetailUiState.Idle,
            )

        val spotDetailUiState: StateFlow<ExploreSpotDetailUiState> =
            spotDetailState.asStateFlow()

        init {
        /*
         * 탐색 메인에서 필요한 API만 즉시 조회합니다.
         *
         * hub / search / detail은
         * 해당 화면 진입 시 호출합니다.
         */
            fetchMainCards()
            fetchExploreCards()
            fetchTrendingCards()
        }

    /*
     * ---------------------------------------------------------
     * 첫 진입 풀스크린 Carousel
     * ---------------------------------------------------------
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
     * ---------------------------------------------------------
     * 전체 관광지 카드
     *
     * GET /api/v1/explore/cards
     * ---------------------------------------------------------
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
                            featuredCards =
                            featuredCards,
                            recommendedCards =
                            recommendedCards,
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
     * ---------------------------------------------------------
     * 지금 뜨는 여행지
     *
     * GET /api/v1/explore/trending
     * ---------------------------------------------------------
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
     * ---------------------------------------------------------
     * 콘텐츠 허브
     *
     * GET /api/v1/explore/hub
     * ---------------------------------------------------------
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
                                        id =
                                            spot.spotId,
                                        title =
                                            spot.name,
                                        location =
                                            spot.location,
                                        popularityRank =
                                            spot.popularityRank,
                                        imageUrl =
                                            spot.imageUrl,
                                        address =
                                            spot.address,
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
     * ---------------------------------------------------------
     * 복합 검색
     *
     * GET /api/v1/explore/search
     * ---------------------------------------------------------
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
                    /*
                     * "전체" 선택은 해당 필터가 없는 것과 동일하게
                     * 서버에는 전달하지 않습니다.
                     */
                        val normalizedTags =
                            if (
                                tags.size ==
                                TagType.entries.size
                            ) {
                                emptySet()
                            } else {
                                tags
                            }

                        val normalizedThemes =
                            if (
                                themes.size ==
                                ThemeType.entries.size
                            ) {
                                emptySet()
                            } else {
                                themes
                            }

                        val normalizedRegions =
                            if (
                                regions.size ==
                                RegionType.entries.size
                            ) {
                                emptySet()
                            } else {
                                regions
                            }

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
                                    normalizedRegions
                                        .map { type ->
                                            type.displayName
                                        }
                                        .takeIf { values ->
                                            values.isNotEmpty()
                                        },
                                themes =
                                    normalizedThemes
                                        .map { type ->
                                            type.displayName
                                        }
                                        .takeIf { values ->
                                            values.isNotEmpty()
                                        },
                                tags =
                                    normalizedTags
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
                            totalCount =
                                response.totalCount,
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
     * ---------------------------------------------------------
     * 자동완성
     * ---------------------------------------------------------
     */
        fun requestAutocomplete(query: String) {
            autocompleteJob?.cancel()

            val normalizedQuery =
                query
                    .trim()
                    .removePrefix("#")

            if (
                normalizedQuery.isBlank()
            ) {
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
                                keyword =
                                normalizedQuery,
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
                                        other =
                                        normalizedQuery,
                                        ignoreCase =
                                        true,
                                    )
                                }
                                .sortedWith(
                                    compareBy<String> { candidate ->
                                        if (
                                            candidate.startsWith(
                                                prefix =
                                                normalizedQuery,
                                                ignoreCase =
                                                true,
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
                                keywords =
                                keywords,
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
     * ---------------------------------------------------------
     * 필터 결과 개수 미리보기
     * ---------------------------------------------------------
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
                        250L,
                    )

                    filterPreviewState.value =
                        ExploreFilterPreviewUiState.Loading

                    try {
                        val normalizedTags =
                            if (
                                tags.size ==
                                TagType.entries.size
                            ) {
                                emptySet()
                            } else {
                                tags
                            }

                        val normalizedThemes =
                            if (
                                themes.size ==
                                ThemeType.entries.size
                            ) {
                                emptySet()
                            } else {
                                themes
                            }

                        val normalizedRegions =
                            if (
                                regions.size ==
                                RegionType.entries.size
                            ) {
                                emptySet()
                            } else {
                                regions
                            }

                        val response =
                            exploreRepository.searchSpots(
                                keyword =
                                null,
                                regions =
                                    normalizedRegions
                                        .map { type ->
                                            type.displayName
                                        }
                                        .takeIf { values ->
                                            values.isNotEmpty()
                                        },
                                themes =
                                    normalizedThemes
                                        .map { type ->
                                            type.displayName
                                        }
                                        .takeIf { values ->
                                            values.isNotEmpty()
                                        },
                                tags =
                                    normalizedTags
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
     * ---------------------------------------------------------
     * 관광지 상세 조회
     *
     * GET /api/v1/explore/spots/{spotId}
     * ---------------------------------------------------------
     */
        fun fetchSpotDetail(spotId: Long) {
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

/*
 * ---------------------------------------------------------
 * 첫 진입 Carousel DTO → UI Model
 * ---------------------------------------------------------
 */
private fun ExploreMainCardDto.toUiModel(): ExploreMainCardUiModel {
    val mappedTheme =
        ThemeType.entries.firstOrNull { type ->
            type.displayName ==
                theme
        } ?: throw IllegalArgumentException(
            "지원하지 않는 관광지 테마입니다: $theme",
        )

    return ExploreMainCardUiModel(
        id =
        spotId,
        title =
        name,
        subTitle =
        subTitle,
        description =
        description,
        location =
        location,
        address =
        address,
        imageUrl =
        imageUrl,
        themeType =
        mappedTheme,
        tags =
            normalizeServerTags(
                tags,
            ),
    )
}

/*
 * ---------------------------------------------------------
 * 일반 관광지 DTO → UI Model
 * ---------------------------------------------------------
 */
private fun ExploreCardDto.toUiModel(): ExploreCardUiModel =
    ExploreCardUiModel(
        id =
        id,
        title =
        name,
        areaCode =
        areaCode,
        areaName =
        areaName,
        themeTag =
        themeTag,
        tier =
        tier,
        imageUrl =
        imageUrl,
        description =
        description,
        mapX =
        mapX,
        mapY =
        mapY,
        address =
        address,
        tags =
            normalizeServerTags(
                tags,
            ),
    )

/*
 * ---------------------------------------------------------
 * 검색 DTO → UI Model
 * ---------------------------------------------------------
 */
private fun ExploreSearchSpotDto.toUiModel(): ExploreSearchSpotUiModel =
    ExploreSearchSpotUiModel(
        id =
        spotId,
        title =
        name,
        location =
        location,
        address =
        address,
        imageUrl =
        imageUrl,
        tags =
            normalizeServerTags(
                tags,
            ),
    )

/*
 * ---------------------------------------------------------
 * 관광지 상세 DTO → UI Model
 * ---------------------------------------------------------
 */
private fun ExploreSpotDetailDto.toUiModel(): ExploreSpotDetailUiModel =
    ExploreSpotDetailUiModel(
        id =
        spotId,
        title =
        name,
        address =
        address,
        tags =
            normalizeServerTags(
                tags,
            ),
        description =
        description,
        operatingHours =
            operatingHours
                .orEmpty()
                .ifBlank {
                    "운영 시간 정보가 없습니다."
                },
        closedDays =
            closedDays
                .orEmpty()
                .ifBlank {
                    "휴무일 정보가 없습니다."
                },
        admissionFee =
            admissionFee
                .orEmpty()
                .ifBlank {
                    "입장료 정보가 없습니다."
                },
        website =
            website
                .orEmpty()
                .ifBlank {
                    "홈페이지 정보가 없습니다."
                },
        phoneNumber =
            phoneNumber
                .orEmpty()
                .ifBlank {
                    "전화번호 정보가 없습니다."
                },
        attractionPoints =
            attractionPoints.map { point ->
                ExploreAttractionPointUiModel(
                    title =
                        point.title,
                    iconType =
                        point.iconType,
                    iconUrl =
                        point.iconUrl,
                )
            },
    )

/*
 * ---------------------------------------------------------
 * 서버 태그 정리
 *
 * 아래 두 경우 모두 처리합니다.
 *
 * ["역사", "궁궐"]
 *
 * ["역사,궁궐,조선왕조,국보"]
 * ---------------------------------------------------------
 */
private fun normalizeServerTags(tags: List<String>): List<String> =
    tags
        .flatMap { tagGroup ->
            tagGroup.split(",")
        }
        .map { tag ->
            tag.trim()
        }
        .filter { tag ->
            tag.isNotBlank()
        }
        .distinct()
