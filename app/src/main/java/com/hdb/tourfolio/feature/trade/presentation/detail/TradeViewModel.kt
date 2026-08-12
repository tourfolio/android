package com.hdb.tourfolio.feature.trade.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.network.TradeRepository
import com.hdb.tourfolio.core.network.TradeType
import com.hdb.tourfolio.core.network.dto.TradeResponseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TradeUiState {
    data object Idle : TradeUiState

    data object Loading : TradeUiState

    data class Success(
        val response: TradeResponseDto,
    ) : TradeUiState

    data class Error(
        val message: String,
    ) : TradeUiState
}

@HiltViewModel
class TradeViewModel
    @Inject
    constructor(
        private val tradeRepository: TradeRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<TradeUiState>(TradeUiState.Idle)
        val uiState: StateFlow<TradeUiState> = _uiState.asStateFlow()

        fun trade(
            spotId: Long,
            type: TradeType,
            quantity: Int,
        ) {
            viewModelScope.launch {
                _uiState.value = TradeUiState.Loading
                _uiState.value =
                    try {
                        TradeUiState.Success(tradeRepository.trade(spotId = spotId, type = type, quantity = quantity))
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        TradeUiState.Error(e.message ?: "거래에 실패했습니다.")
                    }
            }
        }

        fun resetState() {
            _uiState.value = TradeUiState.Idle
        }
    }
