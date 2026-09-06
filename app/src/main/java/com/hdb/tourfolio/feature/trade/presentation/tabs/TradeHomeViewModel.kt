package com.hdb.tourfolio.feature.trade.presentation.tabs

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.portfolio.model.PortfolioSummary
import com.hdb.tourfolio.domain.portfolio.usecase.GetPortfolioSummaryUseCase
import com.hdb.tourfolio.domain.stock.model.RegionalIndex
import com.hdb.tourfolio.domain.stock.model.Stock
import com.hdb.tourfolio.domain.stock.usecase.GetRegionalIndexUseCase
import com.hdb.tourfolio.domain.stock.usecase.GetTopGainersUseCase
import com.hdb.tourfolio.domain.stock.usecase.GetTopLosersUseCase
import com.hdb.tourfolio.feature.trade.presentation.components.PeriodOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

sealed interface RankedStocksUiState {
    data object Loading : RankedStocksUiState

    data class Success(
        val topGainers: List<Stock>,
        val topLosers: List<Stock>,
    ) : RankedStocksUiState

    data class Error(
        val message: String,
    ) : RankedStocksUiState
}

sealed interface PortfolioSummaryUiState {
    data object Loading : PortfolioSummaryUiState

    data class Success(
        val summary: PortfolioSummary,
    ) : PortfolioSummaryUiState

    data class Error(
        val message: String,
    ) : PortfolioSummaryUiState
}

sealed interface RegionalIndexUiState {
    data object Loading : RegionalIndexUiState

    data class Success(
        val items: List<RegionalIndex>,
    ) : RegionalIndexUiState

    data class Error(
        val message: String,
    ) : RegionalIndexUiState
}

/**
 * api/portfolio/summary의 period 파라미터(1W/1M/3M/1Y/ALL)와 1:1 대응한다.
 * 종목 상세의 AssetPeriod(1W/3M/1Y/5Y/ALL)와 지원 기간이 달라 별도 enum으로 분리했다.
 */
enum class PortfolioPeriod(
    override val label: String,
) : PeriodOption {
    WEEK("1주"),
    MONTH("1달"),
    THREE_MONTH("3달"),
    YEAR("1년"),
    ALL("전체"),
}

private fun PortfolioPeriod.toApiPeriod(): String =
    when (this) {
        PortfolioPeriod.WEEK -> "1W"
        PortfolioPeriod.MONTH -> "1M"
        PortfolioPeriod.THREE_MONTH -> "3M"
        PortfolioPeriod.YEAR -> "1Y"
        PortfolioPeriod.ALL -> "ALL"
    }

sealed interface TradeHomeIntent : MviIntent {
    /** 최초 진입 시 급등/급락/포트폴리오 요약을 한 번에 병렬로 불러온다. 전부 끝날 때까지 화면 전체에 로딩을 표시한다. */
    data object FetchHome : TradeHomeIntent

    /** 당겨서 새로고침 — 기존 화면은 그대로 두고 pull-to-refresh 인디케이터만 표시한 채로 다시 불러온다. */
    data object Refresh : TradeHomeIntent

    data object RetryRankedStocks : TradeHomeIntent

    data object RetryRegionalIndex : TradeHomeIntent

    data class SelectPeriod(
        val period: PortfolioPeriod,
    ) : TradeHomeIntent
}

data class TradeHomeState(
    val rankedStocks: RankedStocksUiState = RankedStocksUiState.Loading,
    val portfolioSummary: PortfolioSummaryUiState = PortfolioSummaryUiState.Loading,
    val regionalIndex: RegionalIndexUiState = RegionalIndexUiState.Loading,
    val selectedPeriod: PortfolioPeriod = PortfolioPeriod.WEEK,
    val isRefreshing: Boolean = false,
) : MviState {
    /** 최초 병렬 로딩이 아직 하나도 안 끝난 상태 — 이때만 화면 중앙에 로딩 스피너 하나를 보여준다. */
    val isInitialLoading: Boolean
        get() =
            rankedStocks is RankedStocksUiState.Loading &&
                portfolioSummary is PortfolioSummaryUiState.Loading &&
                regionalIndex is RegionalIndexUiState.Loading
}

sealed interface TradeHomeEffect : MviEffect

@HiltViewModel
class TradeHomeViewModel
    @Inject
    constructor(
        private val getTopGainersUseCase: GetTopGainersUseCase,
        private val getTopLosersUseCase: GetTopLosersUseCase,
        private val getPortfolioSummaryUseCase: GetPortfolioSummaryUseCase,
        private val getRegionalIndexUseCase: GetRegionalIndexUseCase,
    ) : MviViewModel<TradeHomeIntent, TradeHomeState, TradeHomeEffect>(TradeHomeState()) {
        init {
            processIntent(TradeHomeIntent.FetchHome)
        }

        override suspend fun handleIntent(intent: TradeHomeIntent) {
            when (intent) {
                TradeHomeIntent.FetchHome -> fetchHome()
                TradeHomeIntent.Refresh -> refresh()
                TradeHomeIntent.RetryRankedStocks -> fetchRankedStocks()
                TradeHomeIntent.RetryRegionalIndex -> fetchRegionalIndex()
                is TradeHomeIntent.SelectPeriod -> fetchPortfolioSummary(intent.period)
            }
        }

        private suspend fun fetchHome() {
            setState {
                copy(
                    rankedStocks = RankedStocksUiState.Loading,
                    portfolioSummary = PortfolioSummaryUiState.Loading,
                    regionalIndex = RegionalIndexUiState.Loading,
                )
            }

            val (rankedStocksResult, portfolioSummaryResult, regionalIndexResult) = loadHome(currentState.selectedPeriod)
            setState {
                copy(
                    rankedStocks = rankedStocksResult,
                    portfolioSummary = portfolioSummaryResult,
                    regionalIndex = regionalIndexResult,
                )
            }
        }

        private suspend fun refresh() {
            setState { copy(isRefreshing = true) }

            val (rankedStocksResult, portfolioSummaryResult, regionalIndexResult) = loadHome(currentState.selectedPeriod)
            setState {
                copy(
                    rankedStocks = rankedStocksResult,
                    portfolioSummary = portfolioSummaryResult,
                    regionalIndex = regionalIndexResult,
                    isRefreshing = false,
                )
            }
        }

        private suspend fun loadHome(period: PortfolioPeriod): Triple<RankedStocksUiState, PortfolioSummaryUiState, RegionalIndexUiState> =
            coroutineScope {
                val rankedStocksDeferred = async { loadRankedStocks() }
                val portfolioSummaryDeferred = async { loadPortfolioSummary(period) }
                val regionalIndexDeferred = async { loadRegionalIndex() }
                Triple(rankedStocksDeferred.await(), portfolioSummaryDeferred.await(), regionalIndexDeferred.await())
            }

        private suspend fun fetchRankedStocks() {
            setState { copy(rankedStocks = RankedStocksUiState.Loading) }
            val result = loadRankedStocks()
            setState { copy(rankedStocks = result) }
        }

        private suspend fun fetchRegionalIndex() {
            setState { copy(regionalIndex = RegionalIndexUiState.Loading) }
            val result = loadRegionalIndex()
            setState { copy(regionalIndex = result) }
        }

        private suspend fun fetchPortfolioSummary(period: PortfolioPeriod) {
            setState { copy(selectedPeriod = period, portfolioSummary = PortfolioSummaryUiState.Loading) }
            val result = loadPortfolioSummary(period)
            setState { copy(portfolioSummary = result) }
        }

        private suspend fun loadRankedStocks(): RankedStocksUiState =
            try {
                coroutineScope {
                    val topGainersDeferred = async { getTopGainersUseCase() }
                    val topLosersDeferred = async { getTopLosersUseCase() }
                    RankedStocksUiState.Success(
                        topGainers = topGainersDeferred.await(),
                        topLosers = topLosersDeferred.await(),
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                RankedStocksUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }

        private suspend fun loadPortfolioSummary(period: PortfolioPeriod): PortfolioSummaryUiState =
            try {
                PortfolioSummaryUiState.Success(getPortfolioSummaryUseCase(period.toApiPeriod()))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                PortfolioSummaryUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }

        private suspend fun loadRegionalIndex(): RegionalIndexUiState =
            try {
                RegionalIndexUiState.Success(getRegionalIndexUseCase())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                RegionalIndexUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
    }
