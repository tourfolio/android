package com.hdb.tourfolio.feature.trade.presentation.detail

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.portfolio.model.PortfolioItem
import com.hdb.tourfolio.domain.portfolio.usecase.GetCashBalanceUseCase
import com.hdb.tourfolio.domain.portfolio.usecase.GetPortfolioItemUseCase
import com.hdb.tourfolio.domain.portfolio.usecase.RefreshPortfolioUseCase
import com.hdb.tourfolio.domain.stock.model.Stock
import com.hdb.tourfolio.domain.stock.model.StockChartPoint
import com.hdb.tourfolio.domain.stock.usecase.GetStockChartUseCase
import com.hdb.tourfolio.domain.stock.usecase.GetStocksUseCase
import com.hdb.tourfolio.domain.trade.model.TradeResult
import com.hdb.tourfolio.domain.trade.model.TradeType
import com.hdb.tourfolio.domain.trade.usecase.ExecuteTradeUseCase
import com.hdb.tourfolio.domain.watchlist.usecase.GetOrCreateWatchlistStatusUseCase
import com.hdb.tourfolio.domain.watchlist.usecase.ToggleWatchlistUseCase
import com.hdb.tourfolio.feature.trade.presentation.components.AssetPeriod
import com.hdb.tourfolio.feature.trade.presentation.components.AssetPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed interface TradeUiState {
    data object Idle : TradeUiState

    data object Loading : TradeUiState

    data class Success(
        val result: TradeResult,
    ) : TradeUiState

    data class Error(
        val message: String,
    ) : TradeUiState
}

sealed interface StockDetailIntent : MviIntent {
    data class Enter(
        val spotId: Long,
        val keyword: String,
    ) : StockDetailIntent

    data class Trade(
        val spotId: Long,
        val type: TradeType,
        val quantity: Int,
    ) : StockDetailIntent

    data object ResetTradeState : StockDetailIntent

    data class ToggleLike(
        val spotId: Long,
    ) : StockDetailIntent

    data class SelectPeriod(
        val spotId: Long,
        val period: AssetPeriod,
    ) : StockDetailIntent
}

data class StockDetailState(
    val stock: Stock? = null,
    val tradeState: TradeUiState = TradeUiState.Idle,
    val isLiked: Boolean = false,
    val holding: PortfolioItem? = null,
    val cashBalance: Long = 0L,
    val selectedPeriod: AssetPeriod = AssetPeriod.WEEK,
    val chartHistory: List<AssetPoint> = emptyList(),
) : MviState

sealed interface StockDetailEffect : MviEffect

@HiltViewModel
class StockDetailViewModel
    @Inject
    constructor(
        private val executeTradeUseCase: ExecuteTradeUseCase,
        private val getOrCreateWatchlistStatusUseCase: GetOrCreateWatchlistStatusUseCase,
        private val toggleWatchlistUseCase: ToggleWatchlistUseCase,
        private val getPortfolioItemUseCase: GetPortfolioItemUseCase,
        private val getCashBalanceUseCase: GetCashBalanceUseCase,
        private val refreshPortfolioUseCase: RefreshPortfolioUseCase,
        private val getStocksUseCase: GetStocksUseCase,
        private val getStockChartUseCase: GetStockChartUseCase,
    ) : MviViewModel<StockDetailIntent, StockDetailState, StockDetailEffect>(StockDetailState()) {
        override suspend fun handleIntent(intent: StockDetailIntent) {
            when (intent) {
                is StockDetailIntent.Enter -> enter(intent.spotId, intent.keyword)
                is StockDetailIntent.Trade -> trade(intent.spotId, intent.type, intent.quantity)
                StockDetailIntent.ResetTradeState -> setState { copy(tradeState = TradeUiState.Idle) }
                is StockDetailIntent.ToggleLike -> toggleLike(intent.spotId)
                is StockDetailIntent.SelectPeriod -> loadChart(intent.spotId, intent.period)
            }
        }

        private suspend fun enter(
            spotId: Long,
            keyword: String,
        ) {
            coroutineScope {
                launch { loadStock(spotId, keyword) }
                launch { loadWatchlistStatus(spotId) }
                launch { loadHolding(spotId) }
                launch { loadCashBalance() }
                launch { loadChart(spotId, currentState.selectedPeriod) }
            }
        }

        private suspend fun loadChart(
            spotId: Long,
            period: AssetPeriod,
        ) {
            setState { copy(selectedPeriod = period) }
            val history =
                try {
                    getStockChartUseCase(spotId = spotId, period = period.toApiPeriod()).map { it.toAssetPoint() }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    null
                }
            if (history != null) {
                setState { copy(chartHistory = history) }
            }
        }

        private suspend fun loadStock(
            spotId: Long,
            keyword: String,
        ) {
            val stock =
                try {
                    getStocksUseCase(keyword = keyword).firstOrNull { it.id == spotId }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    null
                }
            setState { copy(stock = stock) }
        }

        private suspend fun loadWatchlistStatus(spotId: Long) {
            val liked =
                try {
                    getOrCreateWatchlistStatusUseCase(spotId)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    false
                }
            setState { copy(isLiked = liked) }
        }

        private suspend fun loadHolding(spotId: Long) {
            val holding =
                try {
                    getPortfolioItemUseCase(spotId)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    null
                }
            setState { copy(holding = holding) }
        }

        private suspend fun loadCashBalance() {
            val cashBalance =
                try {
                    getCashBalanceUseCase()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    currentState.cashBalance
                }
            setState { copy(cashBalance = cashBalance) }
        }

        private suspend fun trade(
            spotId: Long,
            type: TradeType,
            quantity: Int,
        ) {
            setState { copy(tradeState = TradeUiState.Loading) }
            val result =
                try {
                    TradeUiState.Success(executeTradeUseCase(spotId, type, quantity))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    TradeUiState.Error(e.message ?: "거래에 실패했습니다.")
                }
            setState { copy(tradeState = result) }

            if (result is TradeUiState.Success) {
                try {
                    refreshPortfolioUseCase()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // 거래는 이미 완료됐으므로 보유 현황 갱신 실패는 무시한다.
                }
                loadHolding(spotId)
                loadCashBalance()
            }
        }

        private suspend fun toggleLike(spotId: Long) {
            val currentlyLiked = currentState.isLiked
            setState { copy(isLiked = !currentlyLiked) }

            val outcome =
                try {
                    toggleWatchlistUseCase(spotId = spotId, currentlyLiked = currentlyLiked)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Result.failure(e)
                }

            outcome.onFailure {
                setState { copy(isLiked = currentlyLiked) }
            }
        }
    }

private fun AssetPeriod.toApiPeriod(): String =
    when (this) {
        AssetPeriod.WEEK -> "1W"
        AssetPeriod.THREE_MONTH -> "3M"
        AssetPeriod.YEAR -> "1Y"
        AssetPeriod.FIVE_YEAR -> "5Y"
        AssetPeriod.ALL -> "ALL"
    }

private fun StockChartPoint.toAssetPoint(): AssetPoint {
    val label = runCatching { LocalDate.parse(date).format(DateTimeFormatter.ofPattern("M/d")) }.getOrDefault(date)
    return AssetPoint(dateLabel = label, value = price)
}
