package com.hdb.tourfolio.feature.trade.presentation.detail

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.stock.model.RegionalIndex
import com.hdb.tourfolio.domain.stock.usecase.GetRegionalIndexUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

sealed interface RegionalIndexDetailRequestState {
    data object Loading : RegionalIndexDetailRequestState

    data class Success(
        val items: List<RegionalIndex>,
    ) : RegionalIndexDetailRequestState

    data class Error(
        val message: String,
    ) : RegionalIndexDetailRequestState
}

sealed interface RegionalIndexDetailIntent : MviIntent {
    data object Fetch : RegionalIndexDetailIntent
}

data class RegionalIndexDetailState(
    val requestState: RegionalIndexDetailRequestState = RegionalIndexDetailRequestState.Loading,
) : MviState

sealed interface RegionalIndexDetailEffect : MviEffect

@HiltViewModel
class RegionalIndexDetailViewModel
    @Inject
    constructor(
        private val getRegionalIndexUseCase: GetRegionalIndexUseCase,
    ) : MviViewModel<RegionalIndexDetailIntent, RegionalIndexDetailState, RegionalIndexDetailEffect>(
            RegionalIndexDetailState(),
        ) {
        init {
            processIntent(RegionalIndexDetailIntent.Fetch)
        }

        override suspend fun handleIntent(intent: RegionalIndexDetailIntent) {
            when (intent) {
                RegionalIndexDetailIntent.Fetch -> fetch()
            }
        }

        private suspend fun fetch() {
            setState { copy(requestState = RegionalIndexDetailRequestState.Loading) }
            val result =
                try {
                    RegionalIndexDetailRequestState.Success(getRegionalIndexUseCase())
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    RegionalIndexDetailRequestState.Error(e.message ?: "오늘의 주요 지수를 불러오지 못했습니다.")
                }
            setState { copy(requestState = result) }
        }
    }
