@file:Suppress("ktlint:standard:function-signature")

package com.hdb.tourfolio.feature.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.network.CardRepository
import com.hdb.tourfolio.core.network.dto.CardDetailDto
import com.hdb.tourfolio.core.network.dto.CardItemDto
import com.hdb.tourfolio.feature.card.mock.CardDetailUiModel
import com.hdb.tourfolio.feature.card.mock.CardListItemUiModel
import com.hdb.tourfolio.feature.card.mock.CardRarity
import com.hdb.tourfolio.feature.explore.model.RegionType
import com.hdb.tourfolio.feature.explore.model.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CARD_ACQUIRE_DISTANCE_METERS = 200f

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

@HiltViewModel
class CardViewModel
    @Inject
    constructor(
        private val cardRepository: CardRepository,
    ) : ViewModel() {
    /*
     * 수집 메인 조회
     */
        private val _uiState =
            MutableStateFlow<CardCollectionUiState>(
                CardCollectionUiState.Loading,
            )

        val uiState: StateFlow<CardCollectionUiState> =
            _uiState.asStateFlow()

    /*
     * 카드 상세 조회
     */
        private val _detailUiState =
            MutableStateFlow<CardDetailUiState>(
                CardDetailUiState.Idle,
            )

        val detailUiState: StateFlow<CardDetailUiState> =
            _detailUiState.asStateFlow()

    /*
     * 위치 검증 / 카드 획득
     */
        private val _acquireUiState =
            MutableStateFlow<CardAcquireUiState>(
                CardAcquireUiState.Idle,
            )

        val acquireUiState: StateFlow<CardAcquireUiState> =
            _acquireUiState.asStateFlow()

    /*
     * 재시도하거나 취소할 때 이전 요청의 결과가 뒤늦게 들어오는 것을 방지
     */
        private var acquireJob: Job? = null

        init {
            fetchCollection()
        }

    /*
     * 수집 메인 조회
     */
        fun fetchCollection(
            region: String? = null,
            theme: String? = null,
            rarity: String? = null,
        ) {
            viewModelScope.launch {
                _uiState.value =
                    CardCollectionUiState.Loading

                _uiState.value =
                    try {
                        val response =
                            cardRepository.getCollection(
                                region = region,
                                theme = theme,
                                rarity = rarity,
                            )

                        CardCollectionUiState.Success(
                            summary =
                                CardCollectionSummary(
                                    collectionRate =
                                        response.collectionRate,
                                    ownedCount =
                                        response.ownedCount,
                                    totalCount =
                                        response.totalCount,
                                ),
                            cards =
                                response.cards.map { card ->
                                    card.toUiModel()
                                },
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        CardCollectionUiState.Error(
                            message =
                                e.message
                                    ?: "카드 목록을 불러오지 못했습니다.",
                        )
                    }
            }
        }

    /*
     * 카드 상세 조회
     */
        fun fetchCardDetail(cardId: Long) {
            viewModelScope.launch {
                _detailUiState.value =
                    CardDetailUiState.Loading

                _detailUiState.value =
                    try {
                        val response =
                            cardRepository.getCardDetail(
                                cardId = cardId,
                            )

                        CardDetailUiState.Success(
                            detail =
                                response.toUiModel(),
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        CardDetailUiState.Error(
                            message =
                                e.message
                                    ?: "카드 상세 정보를 불러오지 못했습니다.",
                        )
                    }
            }
        }

        fun clearCardDetail() {
            _detailUiState.value =
                CardDetailUiState.Idle
        }

    /*
     * 현재 위치 ↔ 관광지 좌표 검증 후 카드 획득
     */
        fun verifyLocationAndAcquire(
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
                    _acquireUiState.value =
                        CardAcquireUiState.Checking

                    try {
                    /*
                     * 1. 관광지 좌표 조회
                     */
                        val location =
                            cardRepository.getCardLocation(
                                cardId = cardId,
                            )

                    /*
                     * 2. 앱 내부 거리 계산
                     */
                        val distanceMeters =
                            calculateDistanceMeters(
                                startLatitude =
                                userLatitude,
                                startLongitude =
                                userLongitude,
                                endLatitude =
                                    location.latitude,
                                endLongitude =
                                    location.longitude,
                            )

                    /*
                     * 3. 200m 초과
                     */
                        if (
                            distanceMeters >
                            CARD_ACQUIRE_DISTANCE_METERS
                        ) {
                            _acquireUiState.value =
                                CardAcquireUiState.TooFar(
                                    spotName =
                                        location.spotName,
                                    distanceMeters =
                                    distanceMeters,
                                )

                            return@launch
                        }

                    /*
                     * 4. 200m 이하
                     */
                        val acquired =
                            cardRepository.acquireCard(
                                cardId = cardId,
                            )

                        _acquireUiState.value =
                            CardAcquireUiState.Success(
                                cardId =
                                    acquired.cardId,
                                cardName =
                                    acquired.cardName,
                                rarity =
                                    acquired.rarity,
                                acquiredAt =
                                    acquired.acquiredAt,
                            )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        _acquireUiState.value =
                            CardAcquireUiState.Error(
                                message =
                                    e.message
                                        ?: "카드 획득 중 오류가 발생했습니다.",
                            )
                    }
                }
        }

    /*
     * 위치 확인 / 카드 획득 흐름 종료 : 진행 중인 API 요청까지 취소합니다.
     */
        fun clearAcquireState() {
            acquireJob?.cancel()
            acquireJob = null

            _acquireUiState.value =
                CardAcquireUiState.Idle
        }
    }

/*
 * 목록 DTO → UI Model
 */
private fun CardItemDto.toUiModel(): CardListItemUiModel =
    CardListItemUiModel(
        id = cardId,
        title = spotName,
        regionType =
            RegionType.entries.firstOrNull { type ->
                type.displayName == region
            } ?: RegionType.SEOUL,
        themeType =
            ThemeType.entries.firstOrNull { type ->
                type.displayName == theme
            } ?: ThemeType.CULTURE,
        rarity =
            runCatching {
                CardRarity.valueOf(rarity)
            }.getOrDefault(
                CardRarity.NORMAL,
            ),
        acquiredDate = null,
        isAcquired = isOwned,
        imageUrl = imageUrl,
        imageRes = null,
    )

/*
 * 상세 DTO → UI Model
 */
private fun CardDetailDto.toUiModel(): CardDetailUiModel {
    val mappedTheme =
        ThemeType.entries.firstOrNull { type ->
            type.displayName == theme
        } ?: throw IllegalArgumentException(
            "지원하지 않는 카드 테마입니다: $theme",
        )

    val mappedRarity =
        runCatching {
            CardRarity.valueOf(rarity)
        }.getOrElse {
            throw IllegalArgumentException(
                "지원하지 않는 카드 희귀도입니다: $rarity",
            )
        }

    return CardDetailUiModel(
        id = cardId,
        title = name,
        themeType = mappedTheme,
        rarity = mappedRarity,
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
}

/*
 * 앱 내부 거리 계산
 */
private fun calculateDistanceMeters(
    startLatitude: Double,
    startLongitude: Double,
    endLatitude: Double,
    endLongitude: Double,
): Float {
    val result =
        FloatArray(1)

    android.location.Location.distanceBetween(
        startLatitude,
        startLongitude,
        endLatitude,
        endLongitude,
        result,
    )

    return result[0]
}
