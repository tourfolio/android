package com.hdb.tourfolio.feature.trade.presentation.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.network.WatchlistRepository
import com.hdb.tourfolio.core.network.dto.WatchlistItemDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WatchlistUiState {
    data object Loading : WatchlistUiState

    data class Success(
        val items: List<WatchlistItemDto>,
    ) : WatchlistUiState

    data class Error(
        val message: String,
    ) : WatchlistUiState
}

@HiltViewModel
class WatchlistViewModel
    @Inject
    constructor(
        private val watchlistRepository: WatchlistRepository,
    ) : ViewModel() {
        private val _uiState =
            MutableStateFlow<WatchlistUiState>(WatchlistUiState.Loading)
        val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

        init {
            fetchWatchlist()
        }

        fun fetchWatchlist() {
            viewModelScope.launch {
                _uiState.value = WatchlistUiState.Loading
                _uiState.value =
                    try {
                        WatchlistUiState.Success(watchlistRepository.getWatchlist())
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        WatchlistUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                    }
            }
        }
    }
