package com.hdb.tourfolio.feature.explore.presentation

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.explore.usecase.GetMainCardsUseCase
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreMainCardUiModel
import com.hdb.tourfolio.feature.explore.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

private const val MAX_CAROUSEL_CARD_COUNT = 3

sealed interface ExploreMainCardsUiState {
    data object Loading : ExploreMainCardsUiState

    data class Success(
        val cards: List<ExploreMainCardUiModel>,
    ) : ExploreMainCardsUiState

    data class Error(
        val message: String,
    ) : ExploreMainCardsUiState
}

sealed interface ExploreCarouselIntent : MviIntent {
    data object FetchMainCards : ExploreCarouselIntent
}

data class ExploreCarouselState(
    val mainCards: ExploreMainCardsUiState = ExploreMainCardsUiState.Loading,
) : MviState

sealed interface ExploreCarouselEffect : MviEffect

@HiltViewModel
class ExploreCarouselViewModel
    @Inject
    constructor(
        private val getMainCardsUseCase: GetMainCardsUseCase,
    ) : MviViewModel<ExploreCarouselIntent, ExploreCarouselState, ExploreCarouselEffect>(ExploreCarouselState()) {
        init {
            processIntent(ExploreCarouselIntent.FetchMainCards)
        }

        override suspend fun handleIntent(intent: ExploreCarouselIntent) {
            when (intent) {
                ExploreCarouselIntent.FetchMainCards -> fetchMainCards()
            }
        }

        private suspend fun fetchMainCards() {
            setState { copy(mainCards = ExploreMainCardsUiState.Loading) }

            val result =
                try {
                    val cards = getMainCardsUseCase()
                    val selectedCards =
                        cards
                            .take(MAX_CAROUSEL_CARD_COUNT)
                            .map { card -> card.toUiModel() }
                    ExploreMainCardsUiState.Success(cards = selectedCards)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    ExploreMainCardsUiState.Error(message = e.message ?: "탐색 카드를 불러오지 못했습니다.")
                }

            setState { copy(mainCards = result) }
        }
    }
