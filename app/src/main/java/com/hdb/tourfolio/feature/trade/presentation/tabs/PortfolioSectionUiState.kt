package com.hdb.tourfolio.feature.trade.presentation.tabs

import com.hdb.tourfolio.domain.portfolio.model.Portfolio

sealed interface PortfolioSectionUiState {
    data object Loading : PortfolioSectionUiState

    data class Success(
        val portfolio: Portfolio,
    ) : PortfolioSectionUiState

    data class Error(
        val message: String,
    ) : PortfolioSectionUiState
}
