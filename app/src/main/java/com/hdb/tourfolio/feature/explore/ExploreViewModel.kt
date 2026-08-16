package com.hdb.tourfolio.feature.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.network.ExploreRepository
import com.hdb.tourfolio.core.network.dto.ExploreCardDto
import com.hdb.tourfolio.core.network.dto.ExploreMainCardDto
import com.hdb.tourfolio.feature.explore.model.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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

sealed interface ExploreTrendingUiState {
    data object Loading : ExploreTrendingUiState

    data class Success(
        val cards: List<ExploreCardUiModel>,
    ) : ExploreTrendingUiState

    data class Error(
        val message: String,
    ) : ExploreTrendingUiState
}

@HiltViewModel
class ExploreViewModel
@Inject
constructor(
    private val exploreRepository: ExploreRepository,
) : ViewModel() {

    private val mainCardsState =
        MutableStateFlow<ExploreMainCardsUiState>(
            ExploreMainCardsUiState.Loading,
        )

    val mainCardsUiState: StateFlow<ExploreMainCardsUiState> =
        mainCardsState.asStateFlow()

    private val exploreCardsState =
        MutableStateFlow<ExploreCardsUiState>(
            ExploreCardsUiState.Loading,
        )

    val exploreCardsUiState: StateFlow<ExploreCardsUiState> =
        exploreCardsState.asStateFlow()

    private val trendingState =
        MutableStateFlow<ExploreTrendingUiState>(
            ExploreTrendingUiState.Loading,
        )

    val trendingUiState: StateFlow<ExploreTrendingUiState> =
        trendingState.asStateFlow()

    init {
        fetchMainCards()
        fetchExploreCards()
        fetchTrendingCards()
    }

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
}

private fun ExploreMainCardDto.toUiModel(): ExploreMainCardUiModel {
    val mappedTheme =
        ThemeType.entries.firstOrNull { type ->
            type.displayName == theme
        } ?: throw IllegalArgumentException(
            "지원하지 않는 관광지 테마입니다: $theme",
        )

    return ExploreMainCardUiModel(
        id = spotId,
        title = name,
        subTitle = subTitle,
        description = description,
        location = location,
        address = address,
        imageUrl = imageUrl,
        themeType = mappedTheme,
        tags = tags,
    )
}

private fun ExploreCardDto.toUiModel(): ExploreCardUiModel =
    ExploreCardUiModel(
        id = id,
        title = name,
        areaCode = areaCode,
        areaName = areaName,
        themeTag = themeTag,
        tier = tier,
        imageUrl = imageUrl,
        description = description,
        mapX = mapX,
        mapY = mapY,
        address = address,
        tags = tags,
    )