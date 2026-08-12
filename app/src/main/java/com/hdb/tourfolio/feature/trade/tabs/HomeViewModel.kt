package com.hdb.tourfolio.feature.trade.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.network.PortfolioRepository
import com.hdb.tourfolio.core.network.StockRepository
import com.hdb.tourfolio.core.network.dto.PortfolioDto
import com.hdb.tourfolio.core.network.dto.StockDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RankedStocksUiState {
    data object Loading : RankedStocksUiState

    data class Success(
        val topGainers: List<StockDto>,
        val topLosers: List<StockDto>,
    ) : RankedStocksUiState

    data class Error(
        val message: String,
    ) : RankedStocksUiState
}

sealed interface PortfolioUiState {
    data object Loading : PortfolioUiState

    data class Success(
        val portfolio: PortfolioDto,
    ) : PortfolioUiState

    data class Error(
        val message: String,
    ) : PortfolioUiState
}

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val stockRepository: StockRepository,
        private val portfolioRepository: PortfolioRepository,
    ) : ViewModel() {
        private val _uiState =
            MutableStateFlow<RankedStocksUiState>(RankedStocksUiState.Loading)
        val uiState: StateFlow<RankedStocksUiState> = _uiState.asStateFlow()

        private val _portfolioUiState =
            MutableStateFlow<PortfolioUiState>(PortfolioUiState.Loading)
        val portfolioUiState: StateFlow<PortfolioUiState> = _portfolioUiState.asStateFlow()

        init {
            fetchRankedStocks()
            fetchPortfolio()
        }

        fun fetchRankedStocks() {
            viewModelScope.launch {
                _uiState.value = RankedStocksUiState.Loading
                _uiState.value =
                    try {
                        val topGainers = stockRepository.getTopGainers()
                        val topLosers = stockRepository.getTopLosers()
                        RankedStocksUiState.Success(
                            topGainers = topGainers,
                            topLosers = topLosers,
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        RankedStocksUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                    }
            }
        }

        fun fetchPortfolio() {
            viewModelScope.launch {
                _portfolioUiState.value = PortfolioUiState.Loading
                _portfolioUiState.value =
                    try {
                        PortfolioUiState.Success(portfolioRepository.getPortfolio())
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        PortfolioUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                    }
            }
        }
    }
