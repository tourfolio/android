package com.hdb.tourfolio.feature.explore.model

/*
 * 탐색 첫 진입 풀스크린 Carousel
 */
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
 * ExploreScreen 관광지 카드
 */
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
 * 지금 뜨는 여행지
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
 * 검색 화면 콘텐츠 허브
 */
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
 * 복합 검색 결과
 */
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
 * 자동완성
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
 * 필터 결과 개수 미리보기
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
 * 관광지 상세
 */
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
