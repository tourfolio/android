package com.hdb.tourfolio.feature.trade.presentation.tabs

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.watchlist.model.WatchlistItem
import com.hdb.tourfolio.domain.watchlist.usecase.GetWatchlistUseCase
import com.hdb.tourfolio.domain.watchlist.usecase.ToggleWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

sealed interface WatchlistUiState {
    data object Loading : WatchlistUiState

    data class Success(
        val items: List<WatchlistItem>,
    ) : WatchlistUiState

    data class Error(
        val message: String,
    ) : WatchlistUiState
}

sealed interface TradeWatchlistIntent : MviIntent {
    data object FetchWatchlist : TradeWatchlistIntent

    data class ToggleLike(
        val spotId: Long,
    ) : TradeWatchlistIntent
}

data class TradeWatchlistState(
    val watchlistResult: WatchlistUiState = WatchlistUiState.Loading,
) : MviState

sealed interface TradeWatchlistEffect : MviEffect

@HiltViewModel
class TradeWatchlistViewModel
    @Inject
    constructor(
        private val getWatchlistUseCase: GetWatchlistUseCase,
        private val toggleWatchlistUseCase: ToggleWatchlistUseCase,
    ) : MviViewModel<TradeWatchlistIntent, TradeWatchlistState, TradeWatchlistEffect>(TradeWatchlistState()) {
        override suspend fun handleIntent(intent: TradeWatchlistIntent) {
            when (intent) {
                TradeWatchlistIntent.FetchWatchlist -> fetchWatchlist()
                is TradeWatchlistIntent.ToggleLike -> toggleLike(intent.spotId)
            }
        }

        private suspend fun fetchWatchlist() {
            setState { copy(watchlistResult = WatchlistUiState.Loading) }
            val result =
                try {
                    WatchlistUiState.Success(getWatchlistUseCase())
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    WatchlistUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                }
            setState { copy(watchlistResult = result) }
        }

        /*
         * 관심 목록에서는 항상 등록된 상태만 보이므로 해제(currentlyLiked=true)만 발생한다.
         * 실패 시 롤백 없이 목록을 그대로 두고, 성공 시에만 로컬에서 즉시 제거한다.
         */
        private suspend fun toggleLike(spotId: Long) {
            val currentState = (currentState.watchlistResult as? WatchlistUiState.Success) ?: return

            val result = toggleWatchlistUseCase(spotId = spotId, currentlyLiked = true)
            result.onSuccess { stillLiked ->
                if (!stillLiked) {
                    val updatedItems = currentState.items.filterNot { it.spotId == spotId }
                    setState { copy(watchlistResult = WatchlistUiState.Success(updatedItems)) }
                }
            }
        }
    }
