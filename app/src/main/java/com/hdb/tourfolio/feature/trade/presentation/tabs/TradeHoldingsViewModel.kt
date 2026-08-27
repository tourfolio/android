package com.hdb.tourfolio.feature.trade.presentation.tabs

import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.portfolio.usecase.ObservePortfolioUseCase
import com.hdb.tourfolio.domain.portfolio.usecase.RefreshPortfolioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TradeHoldingsIntent : MviIntent {
    data object RefreshPortfolio : TradeHoldingsIntent
}

data class TradeHoldingsState(
    val portfolio: PortfolioSectionUiState = PortfolioSectionUiState.Loading,
) : MviState

sealed interface TradeHoldingsEffect : MviEffect

@HiltViewModel
class TradeHoldingsViewModel
    @Inject
    constructor(
        private val observePortfolioUseCase: ObservePortfolioUseCase,
        private val refreshPortfolioUseCase: RefreshPortfolioUseCase,
    ) : MviViewModel<TradeHoldingsIntent, TradeHoldingsState, TradeHoldingsEffect>(TradeHoldingsState()) {
        init {
            /*
             * 포트폴리오는 다른 탭(Home 등)과 캐시를 공유한다.
             * 이미 캐시된 값이 있으면 재요청하지 않는다.
             */
            viewModelScope.launch {
                if (observePortfolioUseCase().first() == null) {
                    processIntent(TradeHoldingsIntent.RefreshPortfolio)
                }
            }

            viewModelScope.launch {
                observePortfolioUseCase().collect { portfolio ->
                    if (portfolio != null) {
                        setState { copy(portfolio = PortfolioSectionUiState.Success(portfolio)) }
                    }
                }
            }
        }

        override suspend fun handleIntent(intent: TradeHoldingsIntent) {
            when (intent) {
                TradeHoldingsIntent.RefreshPortfolio -> refreshPortfolio()
            }
        }

        private suspend fun refreshPortfolio() {
            setState { copy(portfolio = PortfolioSectionUiState.Loading) }
            try {
                refreshPortfolioUseCase()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                setState { copy(portfolio = PortfolioSectionUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")) }
            }
        }
    }
