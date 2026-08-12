package com.hdb.tourfolio.feature.trade.presentation.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.network.PortfolioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HoldingsViewModel
    @Inject
    constructor(
        private val portfolioRepository: PortfolioRepository,
    ) : ViewModel() {
        private val _uiState =
            MutableStateFlow<PortfolioUiState>(PortfolioUiState.Loading)
        val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()

        init {
            fetchPortfolio()
        }

        fun fetchPortfolio() {
            viewModelScope.launch {
                _uiState.value = PortfolioUiState.Loading
                _uiState.value =
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
