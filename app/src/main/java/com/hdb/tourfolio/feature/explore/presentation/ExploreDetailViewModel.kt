package com.hdb.tourfolio.feature.explore.presentation

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.explore.usecase.GetSpotDetailUseCase
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreSpotDetailUiModel
import com.hdb.tourfolio.feature.explore.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

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

sealed interface ExploreDetailIntent : MviIntent {
    data class FetchSpotDetail(
        val spotId: Long,
    ) : ExploreDetailIntent

    data object ClearSpotDetail : ExploreDetailIntent
}

data class ExploreDetailState(
    val spotDetail: ExploreSpotDetailUiState = ExploreSpotDetailUiState.Idle,
) : MviState

sealed interface ExploreDetailEffect : MviEffect

@HiltViewModel
class ExploreDetailViewModel
    @Inject
    constructor(
        private val getSpotDetailUseCase: GetSpotDetailUseCase,
    ) : MviViewModel<ExploreDetailIntent, ExploreDetailState, ExploreDetailEffect>(ExploreDetailState()) {
        override suspend fun handleIntent(intent: ExploreDetailIntent) {
            when (intent) {
                is ExploreDetailIntent.FetchSpotDetail -> fetchSpotDetail(intent.spotId)
                ExploreDetailIntent.ClearSpotDetail -> setState { copy(spotDetail = ExploreSpotDetailUiState.Idle) }
            }
        }

        private suspend fun fetchSpotDetail(spotId: Long) {
            setState { copy(spotDetail = ExploreSpotDetailUiState.Loading) }

            val result =
                try {
                    ExploreSpotDetailUiState.Success(detail = getSpotDetailUseCase(spotId).toUiModel())
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    ExploreSpotDetailUiState.Error(message = e.message ?: "관광지 정보를 불러오지 못했습니다.")
                }

            setState { copy(spotDetail = result) }
        }
    }
