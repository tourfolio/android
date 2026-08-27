package com.hdb.tourfolio.feature.card.presentation

import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.card.model.Card
import com.hdb.tourfolio.domain.card.model.CardDetail
import com.hdb.tourfolio.domain.card.usecase.CardAcquireResult
import com.hdb.tourfolio.domain.card.usecase.GetCardCollectionUseCase
import com.hdb.tourfolio.domain.card.usecase.GetCardDetailUseCase
import com.hdb.tourfolio.domain.card.usecase.VerifyLocationAndAcquireCardUseCase
import com.hdb.tourfolio.domain.common.model.RegionType
import com.hdb.tourfolio.domain.common.model.ThemeType
import com.hdb.tourfolio.feature.card.presentation.model.CardDetailUiModel
import com.hdb.tourfolio.feature.card.presentation.model.CardListItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardCollectionSummary(
    val collectionRate: Double = 0.0,
    val ownedCount: Int = 0,
    val totalCount: Int = 0,
)

sealed interface CardCollectionUiState {
    data object Loading : CardCollectionUiState

    data class Success(
        val summary: CardCollectionSummary,
        val cards: List<CardListItemUiModel>,
    ) : CardCollectionUiState

    data class Error(
        val message: String,
    ) : CardCollectionUiState
}

sealed interface CardDetailUiState {
    data object Idle : CardDetailUiState

    data object Loading : CardDetailUiState

    data class Success(
        val detail: CardDetailUiModel,
    ) : CardDetailUiState

    data class Error(
        val message: String,
    ) : CardDetailUiState
}

sealed interface CardAcquireUiState {
    data object Idle : CardAcquireUiState

    data object Checking : CardAcquireUiState

    data class TooFar(
        val spotName: String,
        val distanceMeters: Float,
    ) : CardAcquireUiState

    data class Success(
        val cardId: Long,
        val cardName: String,
        val rarity: String,
        val acquiredAt: String,
    ) : CardAcquireUiState

    data class Error(
        val message: String,
    ) : CardAcquireUiState
}

sealed interface CardIntent : MviIntent {
    data class FetchCollection(
        val region: String? = null,
        val theme: String? = null,
        val rarity: String? = null,
    ) : CardIntent

    data class FetchCardDetail(
        val cardId: Long,
    ) : CardIntent

    data object ClearCardDetail : CardIntent

    data class VerifyLocationAndAcquire(
        val cardId: Long,
        val userLatitude: Double,
        val userLongitude: Double,
    ) : CardIntent

    data object ClearAcquireState : CardIntent
}

data class CardState(
    val collection: CardCollectionUiState = CardCollectionUiState.Loading,
    val detail: CardDetailUiState = CardDetailUiState.Idle,
    val acquire: CardAcquireUiState = CardAcquireUiState.Idle,
) : MviState

sealed interface CardEffect : MviEffect

@HiltViewModel
class CardViewModel
    @Inject
    constructor(
        private val getCardCollectionUseCase: GetCardCollectionUseCase,
        private val getCardDetailUseCase: GetCardDetailUseCase,
        private val verifyLocationAndAcquireCardUseCase: VerifyLocationAndAcquireCardUseCase,
    ) : MviViewModel<CardIntent, CardState, CardEffect>(CardState()) {
        /*
         * 재시도하거나 취소할 때 이전 요청의 결과가 뒤늦게 들어오는 것을 방지
         */
        private var acquireJob: Job? = null

        init {
            processIntent(CardIntent.FetchCollection())
        }

        override suspend fun handleIntent(intent: CardIntent) {
            when (intent) {
                is CardIntent.FetchCollection -> fetchCollection(intent.region, intent.theme, intent.rarity)
                is CardIntent.FetchCardDetail -> fetchCardDetail(intent.cardId)
                CardIntent.ClearCardDetail -> setState { copy(detail = CardDetailUiState.Idle) }
                is CardIntent.VerifyLocationAndAcquire ->
                    verifyLocationAndAcquire(intent.cardId, intent.userLatitude, intent.userLongitude)
                CardIntent.ClearAcquireState -> clearAcquireState()
            }
        }

        private suspend fun fetchCollection(
            region: String?,
            theme: String?,
            rarity: String?,
        ) {
            setState { copy(collection = CardCollectionUiState.Loading) }
            val result =
                try {
                    val collection = getCardCollectionUseCase(region, theme, rarity)
                    CardCollectionUiState.Success(
                        summary =
                            CardCollectionSummary(
                                collectionRate = collection.collectionRate,
                                ownedCount = collection.ownedCount,
                                totalCount = collection.totalCount,
                            ),
                        cards = collection.cards.map { it.toUiModel() },
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    CardCollectionUiState.Error(e.message ?: "카드 목록을 불러오지 못했습니다.")
                }
            setState { copy(collection = result) }
        }

        private suspend fun fetchCardDetail(cardId: Long) {
            setState { copy(detail = CardDetailUiState.Loading) }
            val result =
                try {
                    CardDetailUiState.Success(getCardDetailUseCase(cardId).toUiModel())
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    CardDetailUiState.Error(e.message ?: "카드 상세 정보를 불러오지 못했습니다.")
                }
            setState { copy(detail = result) }
        }

        private fun verifyLocationAndAcquire(
            cardId: Long,
            userLatitude: Double,
            userLongitude: Double,
        ) {
            /*
             * 위치 확인을 연속으로 누른 경우 기존 작업을 먼저 취소합니다.
             */
            acquireJob?.cancel()

            acquireJob =
                viewModelScope.launch {
                    setState { copy(acquire = CardAcquireUiState.Checking) }

                    val result =
                        try {
                            when (
                                val acquireResult =
                                    verifyLocationAndAcquireCardUseCase(cardId, userLatitude, userLongitude)
                            ) {
                                is CardAcquireResult.TooFar ->
                                    CardAcquireUiState.TooFar(
                                        spotName = acquireResult.spotName,
                                        distanceMeters = acquireResult.distanceMeters.toFloat(),
                                    )

                                is CardAcquireResult.Acquired ->
                                    CardAcquireUiState.Success(
                                        cardId = acquireResult.acquisition.cardId,
                                        cardName = acquireResult.acquisition.cardName,
                                        rarity = acquireResult.acquisition.rarity.name,
                                        acquiredAt = acquireResult.acquisition.acquiredAt,
                                    )
                            }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            CardAcquireUiState.Error(e.message ?: "카드 획득 중 오류가 발생했습니다.")
                        }

                    setState { copy(acquire = result) }
                }
        }

        /*
         * 위치 확인 / 카드 획득 흐름 종료 : 진행 중인 API 요청까지 취소합니다.
         */
        private fun clearAcquireState() {
            acquireJob?.cancel()
            acquireJob = null
            setState { copy(acquire = CardAcquireUiState.Idle) }
        }
    }

private fun Card.toUiModel(): CardListItemUiModel =
    CardListItemUiModel(
        id = cardId,
        title = spotName,
        regionType = RegionType.entries.firstOrNull { it.displayName == region } ?: RegionType.SEOUL,
        themeType = ThemeType.entries.firstOrNull { it.displayName == theme } ?: ThemeType.CULTURE,
        rarity = rarity,
        acquiredDate = null,
        isAcquired = isOwned,
        imageUrl = imageUrl,
        imageRes = null,
    )

private fun CardDetail.toUiModel(): CardDetailUiModel =
    CardDetailUiModel(
        id = cardId,
        title = name,
        themeType = ThemeType.entries.firstOrNull { it.displayName == theme } ?: ThemeType.CULTURE,
        rarity = rarity,
        acquiredDate = acquiredAt,
        isAcquired = isOwned,
        imageUrl = imageUrl,
        imageRes = null,
        address = address,
        glowColorCode = glowColorCode.orEmpty(),
        cardNumber = cardNumber,
        phrase = phrase,
        acquisitionPath = acquisitionPath,
        message = message.orEmpty(),
    )
