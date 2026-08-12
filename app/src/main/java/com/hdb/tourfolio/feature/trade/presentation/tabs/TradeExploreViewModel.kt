package com.hdb.tourfolio.feature.trade.presentation.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.network.StockRepository
import com.hdb.tourfolio.core.network.dto.StockDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ExploreStocksUiState {
    data object Loading : ExploreStocksUiState

    data class Success(
        val stocks: List<StockDto>,
    ) : ExploreStocksUiState

    data class Error(
        val message: String,
    ) : ExploreStocksUiState
}

@HiltViewModel
class TradeExploreViewModel
    @Inject
    constructor(
        private val stockRepository: StockRepository,
    ) : ViewModel() {
        private val _uiState =
            MutableStateFlow<ExploreStocksUiState>(ExploreStocksUiState.Loading)
        val uiState: StateFlow<ExploreStocksUiState> = _uiState.asStateFlow()

        init {
            fetchStocks()
        }

        fun fetchStocks() {
            viewModelScope.launch {
                _uiState.value = ExploreStocksUiState.Loading
                _uiState.value =
                    try {
                        ExploreStocksUiState.Success(stockRepository.getStocks())
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        ExploreStocksUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                    }
            }
        }
    }
