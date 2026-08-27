package com.hdb.tourfolio.feature.explore.presentation

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.explore.usecase.GetExploreCardsUseCase
import com.hdb.tourfolio.domain.explore.usecase.GetTrendingCardsUseCase
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreCardUiModel
import com.hdb.tourfolio.feature.explore.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

private const val FEATURED_CARD_COUNT = 4
private const val RECOMMENDED_CARD_COUNT = 3

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

sealed interface ExploreHomeIntent : MviIntent {
    data object FetchExploreCards : ExploreHomeIntent

    data object FetchTrendingCards : ExploreHomeIntent
}

data class ExploreHomeState(
    val exploreCards: ExploreCardsUiState = ExploreCardsUiState.Loading,
    val trending: ExploreTrendingUiState = ExploreTrendingUiState.Loading,
) : MviState

sealed interface ExploreHomeEffect : MviEffect

@HiltViewModel
class ExploreHomeViewModel
    @Inject
    constructor(
        private val getExploreCardsUseCase: GetExploreCardsUseCase,
        private val getTrendingCardsUseCase: GetTrendingCardsUseCase,
    ) : MviViewModel<ExploreHomeIntent, ExploreHomeState, ExploreHomeEffect>(ExploreHomeState()) {
        init {
            processIntent(ExploreHomeIntent.FetchExploreCards)
            processIntent(ExploreHomeIntent.FetchTrendingCards)
        }

        override suspend fun handleIntent(intent: ExploreHomeIntent) {
            when (intent) {
                ExploreHomeIntent.FetchExploreCards -> fetchExploreCards()
                ExploreHomeIntent.FetchTrendingCards -> fetchTrendingCards()
            }
        }

        private suspend fun fetchExploreCards() {
            setState { copy(exploreCards = ExploreCardsUiState.Loading) }

            val result =
                try {
                    val cards = getExploreCardsUseCase().map { it.toUiModel() }
                    val shuffledCards = cards.shuffled()
                    val featuredCards = shuffledCards.take(FEATURED_CARD_COUNT)
                    val remainingCards = shuffledCards.drop(FEATURED_CARD_COUNT)

                    val recommendedCards =
                        if (remainingCards.size >= RECOMMENDED_CARD_COUNT) {
                            remainingCards.take(RECOMMENDED_CARD_COUNT)
                        } else {
                            cards.shuffled().take(RECOMMENDED_CARD_COUNT)
                        }

                    ExploreCardsUiState.Success(
                        featuredCards = featuredCards,
                        recommendedCards = recommendedCards,
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    ExploreCardsUiState.Error(message = e.message ?: "관광지 목록을 불러오지 못했습니다.")
                }

            setState { copy(exploreCards = result) }
        }

        private suspend fun fetchTrendingCards() {
            setState { copy(trending = ExploreTrendingUiState.Loading) }

            val result =
                try {
                    ExploreTrendingUiState.Success(cards = getTrendingCardsUseCase().map { it.toUiModel() })
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    ExploreTrendingUiState.Error(message = e.message ?: "인기 여행지를 불러오지 못했습니다.")
                }

            setState { copy(trending = result) }
        }
    }
