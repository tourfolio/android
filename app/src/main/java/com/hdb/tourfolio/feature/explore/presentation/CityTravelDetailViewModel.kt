package com.hdb.tourfolio.feature.explore.presentation

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.explore.usecase.GetCityTravelDetailUseCase
import com.hdb.tourfolio.feature.explore.presentation.model.CityTravelDetailUiModel
import com.hdb.tourfolio.feature.explore.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

sealed interface CityTravelDetailUiState {
    data object Loading : CityTravelDetailUiState

    data class Success(
        val detail: CityTravelDetailUiModel,
    ) : CityTravelDetailUiState

    data class Error(
        val message: String,
    ) : CityTravelDetailUiState
}

sealed interface CityTravelDetailIntent : MviIntent {
    data class FetchTravelDetail(
        val travelId: Long,
    ) : CityTravelDetailIntent
}

data class CityTravelDetailState(
    val travelDetail: CityTravelDetailUiState = CityTravelDetailUiState.Loading,
) : MviState

sealed interface CityTravelDetailEffect : MviEffect

@HiltViewModel
class CityTravelDetailViewModel
    @Inject
    constructor(
        private val getCityTravelDetailUseCase: GetCityTravelDetailUseCase,
    ) : MviViewModel<CityTravelDetailIntent, CityTravelDetailState, CityTravelDetailEffect>(CityTravelDetailState()) {
        override suspend fun handleIntent(intent: CityTravelDetailIntent) {
            when (intent) {
                is CityTravelDetailIntent.FetchTravelDetail -> fetchTravelDetail(intent.travelId)
            }
        }

        private suspend fun fetchTravelDetail(travelId: Long) {
            setState { copy(travelDetail = CityTravelDetailUiState.Loading) }

            val result =
                try {
                    val detail = getCityTravelDetailUseCase(travelId)
                    if (detail != null) {
                        CityTravelDetailUiState.Success(detail = detail.toUiModel())
                    } else {
                        CityTravelDetailUiState.Error(message = "추천여행 정보를 찾을 수 없습니다.")
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    CityTravelDetailUiState.Error(message = e.message ?: "추천여행 정보를 불러오지 못했습니다.")
                }

            setState { copy(travelDetail = result) }
        }
    }
