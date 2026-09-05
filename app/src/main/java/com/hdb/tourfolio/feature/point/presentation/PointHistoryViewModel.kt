package com.hdb.tourfolio.feature.point.presentation

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.point.model.PointHistory
import com.hdb.tourfolio.domain.point.usecase.GetPointHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/*
 * 포인트 내역 조회 상태
 */
sealed interface PointHistoryRequestState {
    data object Loading : PointHistoryRequestState

    data class Success(
        val pointHistory: PointHistory,
    ) : PointHistoryRequestState

    data class Error(
        val message: String,
    ) : PointHistoryRequestState
}

sealed interface PointHistoryIntent : MviIntent {
    data object FetchPointHistory : PointHistoryIntent
}

data class PointHistoryState(
    val historyState: PointHistoryRequestState =
        PointHistoryRequestState.Loading,
) : MviState

sealed interface PointHistoryEffect : MviEffect

@HiltViewModel
class PointHistoryViewModel
    @Inject
    constructor(
        private val getPointHistoryUseCase: GetPointHistoryUseCase,
    ) : MviViewModel<PointHistoryIntent, PointHistoryState, PointHistoryEffect>(
            PointHistoryState(),
        ) {
        override suspend fun handleIntent(intent: PointHistoryIntent) {
            when (intent) {
                PointHistoryIntent.FetchPointHistory ->
                    fetchPointHistory()
            }
        }

        private suspend fun fetchPointHistory() {
            setState {
                copy(
                    historyState =
                        PointHistoryRequestState.Loading,
                )
            }

            val result =
                try {
                    PointHistoryRequestState.Success(
                        pointHistory =
                            getPointHistoryUseCase(),
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    PointHistoryRequestState.Error(
                        message =
                            e.message
                                ?: "포인트 내역을 불러오지 못했습니다.",
                    )
                }

            setState {
                copy(
                    historyState = result,
                )
            }
        }
    }
