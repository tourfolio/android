package com.hdb.tourfolio.feature.trade.presentation.tabs

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.stock.model.Stock
import com.hdb.tourfolio.domain.stock.usecase.GetStocksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

sealed interface ExploreStocksUiState {
    data object Loading : ExploreStocksUiState

    data class Success(
        val stocks: List<Stock>,
    ) : ExploreStocksUiState

    data class Error(
        val message: String,
    ) : ExploreStocksUiState
}

enum class TradeExploreSortOption(
    val label: String,
) {
    CHANGE_RATE("등락률"),
    VOLUME("거래량"),
    PRICE("가격순"),
}

/*
 * TODO(backend): GET /api/stocks 응답에 거래량 필드가 없어 서버 정렬 대상이 아님(sortBy는 price/changeRate/name/tier만 지원).
 */
private fun TradeExploreSortOption.toApiSortByOrNull(): String? =
    when (this) {
        TradeExploreSortOption.CHANGE_RATE -> "changeRate"
        TradeExploreSortOption.PRICE -> "price"
        TradeExploreSortOption.VOLUME -> null
    }

sealed interface TradeExploreIntent : MviIntent {
    data object FetchStocks : TradeExploreIntent

    data class SelectRegion(
        val region: String,
    ) : TradeExploreIntent

    data class SelectCategory(
        val category: String,
    ) : TradeExploreIntent

    data class SelectSortOption(
        val option: TradeExploreSortOption,
    ) : TradeExploreIntent
}

data class TradeExploreState(
    val stocksResult: ExploreStocksUiState = ExploreStocksUiState.Loading,
    val selectedRegion: String = ALL_FILTER,
    val selectedCategory: String = ALL_FILTER,
    val selectedSortOption: TradeExploreSortOption = TradeExploreSortOption.CHANGE_RATE,
) : MviState {
    /*
     * region/category(tags)는 서버 쿼리 파라미터로 필터링됨(fetchStocks 참고).
     * 여기서는 정렬만 클라이언트에서 다시 적용한다.
     */
    val filteredStocks: List<Stock>
        get() {
            val stocks = (stocksResult as? ExploreStocksUiState.Success)?.stocks ?: emptyList()

            return when (selectedSortOption) {
                TradeExploreSortOption.CHANGE_RATE -> stocks.sortedByDescending { it.changeRate }
                TradeExploreSortOption.PRICE -> stocks.sortedByDescending { it.currentPrice }
                /*
                 * TODO(backend): GET /api/stocks 응답에 거래량 필드가 없어 정렬 불가 — 원본 순서 유지.
                 */
                TradeExploreSortOption.VOLUME -> stocks
            }
        }

    companion object {
        const val ALL_FILTER = "전체"
    }
}

sealed interface TradeExploreEffect : MviEffect

@HiltViewModel
class TradeExploreViewModel
    @Inject
    constructor(
        private val getStocksUseCase: GetStocksUseCase,
    ) : MviViewModel<TradeExploreIntent, TradeExploreState, TradeExploreEffect>(TradeExploreState()) {
        init {
            processIntent(TradeExploreIntent.FetchStocks)
        }

        override suspend fun handleIntent(intent: TradeExploreIntent) {
            when (intent) {
                TradeExploreIntent.FetchStocks -> fetchStocks()

                is TradeExploreIntent.SelectRegion -> {
                    setState { copy(selectedRegion = intent.region) }
                    fetchStocks()
                }

                is TradeExploreIntent.SelectCategory -> {
                    setState { copy(selectedCategory = intent.category) }
                    fetchStocks()
                }

                is TradeExploreIntent.SelectSortOption -> {
                    setState { copy(selectedSortOption = intent.option) }
                    if (intent.option.toApiSortByOrNull() != null) fetchStocks()
                }
            }
        }

        private suspend fun fetchStocks() {
            setState { copy(stocksResult = ExploreStocksUiState.Loading) }
            val region = if (currentState.selectedRegion == TradeExploreState.ALL_FILTER) "ALL" else currentState.selectedRegion
            val tags =
                if (currentState.selectedCategory == TradeExploreState.ALL_FILTER) {
                    null
                } else {
                    listOf(currentState.selectedCategory)
                }
            val sortBy = currentState.selectedSortOption.toApiSortByOrNull() ?: "changeRate"
            val result =
                try {
                    ExploreStocksUiState.Success(
                        getStocksUseCase(region = region, tags = tags, sortBy = sortBy, sortOrder = "DESC"),
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    ExploreStocksUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                }
            setState { copy(stocksResult = result) }
        }
    }
