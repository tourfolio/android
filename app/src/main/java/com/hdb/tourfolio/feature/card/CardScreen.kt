@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.feature.card.components.CardAcquisitionSuccessScreen
import com.hdb.tourfolio.feature.card.components.CardDetailBottomSheet
import com.hdb.tourfolio.feature.card.components.CardFilterBar
import com.hdb.tourfolio.feature.card.components.CardFilterState
import com.hdb.tourfolio.feature.card.components.CardHeader
import com.hdb.tourfolio.feature.card.components.LocationDialogType
import com.hdb.tourfolio.feature.card.components.LocationPermissionDialog
import com.hdb.tourfolio.feature.card.components.LocationPermissionRequiredDialog
import com.hdb.tourfolio.feature.card.components.LocationVerificationAnimationScreen
import com.hdb.tourfolio.feature.card.components.LocationVerificationAnimationState
import com.hdb.tourfolio.feature.card.components.OwnedCardStatus
import com.hdb.tourfolio.feature.card.components.TourCard
import com.hdb.tourfolio.feature.card.components.getCurrentPreciseLocation
import com.hdb.tourfolio.feature.card.components.isPreciseLocationGranted
import com.hdb.tourfolio.feature.card.components.shouldShowLocationPermissionFlow
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun CardScreen(
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCardClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = hiltViewModel(),
) {
    val context =
        LocalContext.current

    val coroutineScope =
        rememberCoroutineScope()

    /*
     * 수집 메인 조회 상태
     */
    val collectionUiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    /*
     * 카드 상세 조회 상태
     */
    val detailUiState by
    viewModel.detailUiState.collectAsStateWithLifecycle()

    /*
     * 위치 검증 / 카드 획득 상태
     */
    val acquireUiState by
    viewModel.acquireUiState.collectAsStateWithLifecycle()

    var filterState by remember {
        mutableStateOf(
            CardFilterState(),
        )
    }

    /*
     * 현재 선택된 카드
     */
    var selectedCardId by remember {
        mutableStateOf<Long?>(null)
    }

    /*
     * 위치 권한 Dialog
     */
    var locationDialogType by remember {
        mutableStateOf<LocationDialogType?>(null)
    }

    /*
     * 위치 검증 전체 화면 표시 여부
     */
    var showLocationVerificationScreen by remember {
        mutableStateOf(false)
    }

    /*
     * 사용자 현재 위치를 요청하고 있는 Coroutine입니다.
     *
     * 사용자가 CHECKING 화면에서 취소할 경우
     * 현재 위치 조회 작업도 같이 취소합니다.
     */
    var currentLocationJob by remember {
        mutableStateOf<Job?>(null)
    }

    /*
     * GPS 자체를 가져오지 못했을 때의
     * 로컬 오류 메시지입니다.
     */
    var locationErrorMessage by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * ---------------------------------------------------------
     * 실제 위치 확인 시작
     * ---------------------------------------------------------
     *
     * 1. CHECKING 화면 표시
     * 2. 사용자 현재 GPS 획득
     * 3. ViewModel에 사용자 좌표 전달
     * 4. ViewModel:
     *    - 관광지 좌표 API
     *    - 거리 계산
     *    - 200m 초과 → TooFar
     *    - 200m 이하 → acquire API
     */
    fun startLocationVerification() {
        val cardId =
            selectedCardId
                ?: return

        /*
         * 이전 위치 조회가 남아 있다면 취소
         */
        currentLocationJob?.cancel()

        /*
         * 이전 획득 상태 초기화
         */
        viewModel.clearAcquireState()

        locationErrorMessage =
            null

        /*
         * 즉시 CHECKING 화면 표시
         */
        showLocationVerificationScreen =
            true

        currentLocationJob =
            coroutineScope.launch {
                val currentLocation =
                    getCurrentPreciseLocation(
                        context = context,
                    )

                if (currentLocation == null) {
                    locationErrorMessage =
                        "현재 위치를 확인할 수 없습니다.\nGPS가 켜져 있는지 확인해주세요."

                    return@launch
                }

                /*
                 * 실제 카드 획득 검증 시작
                 *
                 * 사용자 위/경도는 이 함수에서
                 * 서버가 아니라 ViewModel에만 전달합니다.
                 */
                viewModel.verifyLocationAndAcquire(
                    cardId = cardId,
                    userLatitude =
                        currentLocation.latitude,
                    userLongitude =
                        currentLocation.longitude,
                )
            }
    }

    /*
     * ---------------------------------------------------------
     * 카드 획득 성공 화면
     * ---------------------------------------------------------
     */
    if (acquireUiState is CardAcquireUiState.Success) {
        val success =
            acquireUiState as CardAcquireUiState.Success

        CardAcquisitionSuccessScreen(
            cardName =
                success.cardName,
            rarity =
                success.rarity,
            acquiredAt =
                success.acquiredAt,
            cardId =
                success.cardId,

            /*
             * 이미 CardScreen 안에 있으므로
             * 컬렉션에서 보기는 성공 화면을 종료하고
             * 최신 카드 목록을 다시 조회합니다.
             */
            onCollectionClick = {
                selectedCardId =
                    null

                showLocationVerificationScreen =
                    false

                viewModel.clearCardDetail()
                viewModel.clearAcquireState()

                viewModel.fetchCollection(
                    region =
                        filterState.region
                            ?.displayName,
                    theme =
                        filterState.theme
                            ?.displayName,
                    rarity =
                        filterState.rarity
                            ?.name,
                )
            },

            /*
             * 닫기도 수집 메인으로 돌아옵니다.
             */
            onCloseClick = {
                selectedCardId =
                    null

                showLocationVerificationScreen =
                    false

                viewModel.clearCardDetail()
                viewModel.clearAcquireState()

                viewModel.fetchCollection(
                    region =
                        filterState.region
                            ?.displayName,
                    theme =
                        filterState.theme
                            ?.displayName,
                    rarity =
                        filterState.rarity
                            ?.name,
                )
            },
            modifier =
                modifier,
        )

        return
    }

    /*
     * ---------------------------------------------------------
     * 위치 검증 전체 화면
     * ---------------------------------------------------------
     */
    if (showLocationVerificationScreen) {
        /*
         * 현재 GPS 획득 자체가 실패한 경우
         */
        if (locationErrorMessage != null) {
            CardError(
                title =
                    "위치를 확인하지 못했습니다.",
                message =
                    locationErrorMessage.orEmpty(),
                onRetryClick = {
                    startLocationVerification()
                },
                onDismissClick = {
                    currentLocationJob?.cancel()

                    locationErrorMessage =
                        null

                    showLocationVerificationScreen =
                        false

                    viewModel.clearAcquireState()
                },
            )

            return
        }

        /*
         * 관광지 좌표 조회 또는 카드 획득 API 실패
         */
        if (acquireUiState is CardAcquireUiState.Error) {
            val error =
                acquireUiState as CardAcquireUiState.Error

            CardError(
                title =
                    "카드 획득을 진행하지 못했습니다.",
                message =
                    error.message,
                onRetryClick = {
                    startLocationVerification()
                },
                onDismissClick = {
                    currentLocationJob?.cancel()

                    showLocationVerificationScreen =
                        false

                    viewModel.clearAcquireState()
                },
            )

            return
        }

        /*
         * TooFar일 때만 TOO_FAR,
         * 나머지는 모두 CHECKING 상태입니다.
         *
         * 즉 GPS를 얻는 동안도 CHECKING이고,
         * 관광지 좌표 API를 호출하는 동안도 CHECKING이며,
         * acquire API 응답을 기다리는 동안도 CHECKING입니다.
         */
        val verificationState =
            when (acquireUiState) {
                is CardAcquireUiState.TooFar ->
                    LocationVerificationAnimationState.TOO_FAR

                else ->
                    LocationVerificationAnimationState.CHECKING
            }

        /*
         * API가 관광지 이름을 반환했다면 그것을 사용하고,
         * 아직 조회 전이면 상세 API의 관광지명을 사용합니다.
         */
        val spotName =
            when (val state = acquireUiState) {
                is CardAcquireUiState.TooFar ->
                    state.spotName

                else ->
                    (
                            detailUiState as?
                                    CardDetailUiState.Success
                            )?.detail?.title
                        ?: "관광지"
            }

        LocationVerificationAnimationScreen(
            state =
                verificationState,
            spotName =
                spotName,

            /*
             * CHECKING → 취소하기
             *
             * 카드 상세 상태는 유지하기 때문에
             * 다시 상세 BottomSheet로 돌아갑니다.
             */
            onCancelClick = {
                currentLocationJob?.cancel()

                showLocationVerificationScreen =
                    false

                locationErrorMessage =
                    null

                viewModel.clearAcquireState()
            },

            /*
             * TOO_FAR → 위치 확인
             *
             * GPS부터 다시 가져와
             * 거리 계산을 처음부터 수행합니다.
             */
            onCheckAgainClick = {
                startLocationVerification()
            },

            /*
             * TOO_FAR → 돌아가기
             *
             * 카드 상세까지 닫고 수집 메인으로 돌아갑니다.
             */
            onBackClick = {
                currentLocationJob?.cancel()

                showLocationVerificationScreen =
                    false

                locationErrorMessage =
                    null

                selectedCardId =
                    null

                viewModel.clearAcquireState()
                viewModel.clearCardDetail()
            },
            modifier =
                modifier,
        )

        return
    }

    /*
     * ---------------------------------------------------------
     * 수집 메인 API
     * ---------------------------------------------------------
     */
    when (val state = collectionUiState) {
        CardCollectionUiState.Loading -> {
            CardLoading(
                modifier = modifier,
            )
        }

        is CardCollectionUiState.Error -> {
            CardError(
                title =
                    "카드 목록을 불러오지 못했습니다.",
                message =
                    state.message,
                onRetryClick = {
                    viewModel.fetchCollection(
                        region =
                            filterState.region
                                ?.displayName,
                        theme =
                            filterState.theme
                                ?.displayName,
                        rarity =
                            filterState.rarity
                                ?.name,
                    )
                },
                modifier =
                    modifier,
            )
        }

        is CardCollectionUiState.Success -> {
            val summary =
                state.summary

            val cards =
                state.cards

            Column(
                modifier =
                    modifier
                        .fillMaxSize()
                        .background(
                            Natural100,
                        ),
            ) {
                Column(
                    modifier =
                        Modifier.padding(
                            start = 22.dp,
                            top = 20.dp,
                            end = 22.dp,
                        ),
                ) {
                    CardHeader(
                        onProfileClick =
                            onProfileClick,
                        onNotificationClick =
                            onNotificationClick,
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                28.dp,
                            ),
                    )

                    OwnedCardStatus(
                        ownedCardCount =
                            summary.ownedCount,
                        totalCardCount =
                            summary.totalCount,
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                34.dp,
                            ),
                    )

                    /*
                     * -------------------------------------------------
                     * 서버 필터
                     * -------------------------------------------------
                     */
                    CardFilterBar(
                        filterState =
                            filterState,

                        onAllClick = {
                            filterState =
                                CardFilterState()

                            viewModel.fetchCollection()
                        },

                        onRegionSelected = { region ->
                            val newFilterState =
                                filterState.copy(
                                    region = region,
                                )

                            filterState =
                                newFilterState

                            viewModel.fetchCollection(
                                region =
                                    newFilterState
                                        .region
                                        ?.displayName,
                                theme =
                                    newFilterState
                                        .theme
                                        ?.displayName,
                                rarity =
                                    newFilterState
                                        .rarity
                                        ?.name,
                            )
                        },

                        onThemeSelected = { theme ->
                            val newFilterState =
                                filterState.copy(
                                    theme = theme,
                                )

                            filterState =
                                newFilterState

                            viewModel.fetchCollection(
                                region =
                                    newFilterState
                                        .region
                                        ?.displayName,
                                theme =
                                    newFilterState
                                        .theme
                                        ?.displayName,
                                rarity =
                                    newFilterState
                                        .rarity
                                        ?.name,
                            )
                        },

                        onRaritySelected = { rarity ->
                            val newFilterState =
                                filterState.copy(
                                    rarity = rarity,
                                )

                            filterState =
                                newFilterState

                            viewModel.fetchCollection(
                                region =
                                    newFilterState
                                        .region
                                        ?.displayName,
                                theme =
                                    newFilterState
                                        .theme
                                        ?.displayName,
                                rarity =
                                    newFilterState
                                        .rarity
                                        ?.name,
                            )
                        },
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                18.dp,
                            ),
                    )

                    Text(
                        text =
                            "총 ${cards.size}장",
                        style =
                            LocalAppTypography
                                .current
                                .bodySmall
                                .medium
                                .copy(
                                    color =
                                        Natural60,
                                ),
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                10.dp,
                            ),
                    )
                }

                LazyVerticalGrid(
                    columns =
                        GridCells.Fixed(
                            count = 3,
                        ),
                    modifier =
                        Modifier.fillMaxSize(),
                    contentPadding =
                        PaddingValues(
                            start = 22.dp,
                            end = 22.dp,
                            bottom = 24.dp,
                        ),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp,
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            8.dp,
                        ),
                ) {
                    items(
                        items =
                            cards,
                        key = { item ->
                            item.id
                        },
                    ) { item ->
                        TourCard(
                            item =
                                item,
                            onClick = { cardId ->
                                selectedCardId =
                                    cardId

                                viewModel.fetchCardDetail(
                                    cardId =
                                        cardId,
                                )

                                onCardClick(
                                    cardId,
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    /*
     * ---------------------------------------------------------
     * 카드 상세 API
     * ---------------------------------------------------------
     */
    when (val detailState = detailUiState) {
        CardDetailUiState.Idle ->
            Unit

        CardDetailUiState.Loading -> {
            CardLoading(
                overlay = true,
            )
        }

        is CardDetailUiState.Error -> {
            CardError(
                title =
                    "카드 정보를 불러오지 못했습니다.",
                message =
                    detailState.message,
                onRetryClick = {
                    selectedCardId?.let { cardId ->
                        viewModel.fetchCardDetail(
                            cardId =
                                cardId,
                        )
                    }
                },
                onDismissClick = {
                    selectedCardId =
                        null

                    viewModel.clearCardDetail()
                },
                overlay = true,
            )
        }

        is CardDetailUiState.Success -> {
            CardDetailBottomSheet(
                card =
                    detailState.detail,

                onDismissRequest = {
                    selectedCardId =
                        null

                    viewModel.clearCardDetail()
                    viewModel.clearAcquireState()
                },

                onExpandImageClick = {
                    /*
                     * 추후 카드 확대 기능
                     */
                },

                /*
                 * -------------------------------------------------
                 * 카드 획득하기
                 * -------------------------------------------------
                 */
                onAcquireClick = {
                    when {
                        /*
                         * 이미 정확한 위치 권한이 있는 경우
                         *
                         * 바로 실제 위치 검증 시작
                         */
                        isPreciseLocationGranted(
                            context = context,
                        ) -> {
                            startLocationVerification()
                        }

                        /*
                         * 아직 시스템 위치 권한을
                         * 요청할 수 있는 상태
                         */
                        shouldShowLocationPermissionFlow(
                            context = context,
                        ) -> {
                            locationDialogType =
                                LocationDialogType.PERMISSION_REQUEST
                        }

                        /*
                         * 반복 거부 등으로
                         * 더 이상 시스템 Dialog를 띄우기 어려운 상태
                         */
                        else -> {
                            locationDialogType =
                                LocationDialogType.PERMISSION_REQUIRED
                        }
                    }
                },
            )
        }
    }

    /*
     * ---------------------------------------------------------
     * 위치 권한 Dialog
     * ---------------------------------------------------------
     */
    when (locationDialogType) {
        LocationDialogType.PERMISSION_REQUEST -> {
            LocationPermissionDialog(
                onDismissRequest = {
                    locationDialogType =
                        null
                },

                /*
                 * 시스템 권한 허용 완료
                 *
                 * 이제 실제 위치 검증 시작
                 */
                onPreciseLocationGranted = {
                    locationDialogType =
                        null

                    startLocationVerification()
                },

                onPreciseLocationDenied = {
                    locationDialogType =
                        null
                },
            )
        }

        LocationDialogType.PERMISSION_REQUIRED -> {
            LocationPermissionRequiredDialog(
                onDismissRequest = {
                    locationDialogType =
                        null
                },
            )
        }

        null ->
            Unit
    }
}

/*
 * ---------------------------------------------------------
 * 공통 Loading
 * ---------------------------------------------------------
 */
@Composable
private fun CardLoading(
    modifier: Modifier = Modifier,
    overlay: Boolean = false,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    color =
                        if (overlay) {
                            Color.Black.copy(
                                alpha = 0.35f,
                            )
                        } else {
                            Natural100
                        },
                ),
        contentAlignment =
            Alignment.Center,
    ) {
        CircularProgressIndicator(
            color =
                Primary,
        )
    }
}

/*
 * ---------------------------------------------------------
 * 공통 Error
 * ---------------------------------------------------------
 */
@Composable
private fun CardError(
    title: String,
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissClick: (() -> Unit)? = null,
    overlay: Boolean = false,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    color =
                        if (overlay) {
                            Color.Black.copy(
                                alpha = 0.35f,
                            )
                        } else {
                            Natural100
                        },
                ),
        contentAlignment =
            Alignment.Center,
    ) {
        Column(
            modifier =
                if (overlay) {
                    Modifier
                        .padding(
                            horizontal = 34.dp,
                        )
                        .background(
                            color =
                                Natural100,
                            shape =
                                androidx.compose.foundation.shape
                                    .RoundedCornerShape(
                                        16.dp,
                                    ),
                        )
                        .padding(
                            horizontal = 28.dp,
                            vertical = 24.dp,
                        )
                } else {
                    Modifier.padding(
                        horizontal = 28.dp,
                    )
                },
            horizontalAlignment =
                Alignment.CenterHorizontally,
        ) {
            Text(
                text =
                    title,
                style =
                    LocalAppTypography
                        .current
                        .bodyLarge
                        .bold
                        .copy(
                            color =
                                Natural10,
                        ),
            )

            Spacer(
                modifier =
                    Modifier.height(
                        8.dp,
                    ),
            )

            Text(
                text =
                    message,
                style =
                    LocalAppTypography
                        .current
                        .bodySmall
                        .medium
                        .copy(
                            color =
                                Natural60,
                        ),
            )

            Spacer(
                modifier =
                    Modifier.height(
                        12.dp,
                    ),
            )

            TextButton(
                onClick =
                    onRetryClick,
            ) {
                Text(
                    text =
                        "다시 시도",
                    style =
                        LocalAppTypography
                            .current
                            .bodySmall
                            .bold
                            .copy(
                                color =
                                    Primary,
                            ),
                )
            }

            if (onDismissClick != null) {
                TextButton(
                    onClick =
                        onDismissClick,
                ) {
                    Text(
                        text =
                            "닫기",
                        style =
                            LocalAppTypography
                                .current
                                .bodySmall
                                .medium
                                .copy(
                                    color =
                                        Natural60,
                                ),
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Card Main Screen",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun CardScreenPreview() {
    TourfolioTheme {
        /*
         * 실제 API/Hilt에 의존하므로
         * 현재 CardScreen 자체 Preview는 생략합니다.
         */
    }
}